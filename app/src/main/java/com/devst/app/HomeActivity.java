package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.provider.Settings;

public class HomeActivity extends AppCompatActivity {

    // Variables
    private String emailUsuario = "";
    private TextView tvBienvenida;

    // Linterna / Cámara (trasera con flash)
    private Button btnLinterna;
    private CameraManager camara;
    private String camaraID = null;
    private boolean luz = false;

    // ===== Activity Results =====
    // Recibir datos de PerfilActivity
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String nombre = result.getData().getStringExtra("nombre_editado");
                    if (nombre != null) {
                        tvBienvenida.setText("Hola, " + nombre);
                    }
                }
            });

    // Pedir permiso de cámara en tiempo de ejecución (linterna)
    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    alternarluz();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    // Elegir imagen de la galería
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Toast.makeText(this, "Imagen seleccionada: " + uri, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Referencias existentes
        tvBienvenida     = findViewById(R.id.tvBienvenida);
        Button btnIrPerfil     = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb     = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir    = findViewById(R.id.btnCompartir);
        btnLinterna            = findViewById(R.id.btnLinterna);
        Button btnCamara       = findViewById(R.id.btnCamara);

        // Referencias nuevas que añadí
        Button btnCamaraFrontal = findViewById(R.id.btnCamaraFrontal);
        Button btnMaps          = findViewById(R.id.btnMaps);
        Button btnDial          = findViewById(R.id.btnDial);
        Button btnGaleria       = findViewById(R.id.btnGaleria);
        Button btnBluetooth     = findViewById(R.id.btnBluetooth);

        // Recibir dato del Login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenido: " + emailUsuario);


        // Home -> Perfil (esperando resultado)
        btnIrPerfil.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, PerfilActivity.class);
            i.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(i);
        });

        // Home -> CamaraActivity (explícito)
        btnCamara.setOnClickListener(v ->
                startActivity(new Intent(this, CamaraActivity.class))
        );


        // Abrir web
        btnAbrirWeb.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.santotomas.cl");
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(viewWeb);
        });

        // Enviar correo
        btnEnviarCorreo.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:")); // Solo apps de correo
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            email.putExtra(Intent.EXTRA_SUBJECT, "Prueba desde la app");
            email.putExtra(Intent.EXTRA_TEXT, "Hola, esto es un intento de correo.");
            startActivity(Intent.createChooser(email, "Enviar correo con:"));
        });

        // Compartir texto
        btnCompartir.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Hola desde mi app Android 😎");
            startActivity(Intent.createChooser(share, "Compartir usando:"));
        });

        // ====== Linterna (con permiso) ======
        camara = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (String id : camara.getCameraIdList()) {
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean disponibleFlash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer lensFacing = cc.get(CameraCharacteristics.LENS_FACING);
                if (Boolean.TRUE.equals(disponibleFlash)
                        && lensFacing != null
                        && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                    camaraID = id; // prioriza la cámara trasera con flash
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
        }

        btnLinterna.setOnClickListener(v -> {
            if (camaraID == null) {
                Toast.makeText(this, "Este dispositivo no tiene flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean camGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;

            if (camGranted) {
                alternarluz();
            } else {
                permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // ====== Nuevos Implicitos que añadí ======
        if (btnCamaraFrontal != null) {
            btnCamaraFrontal.setOnClickListener(v -> abrirCamaraFrontalSeguro());
        }

        if (btnMaps != null) {
            btnMaps.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Vergara+165,+Santiago,+Chile+(Instituto+Profesional+Santo+Tomás)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps"); // Abre directamente Google Maps
                startActivity(mapIntent);
            });
        }


        if (btnDial != null) {
            btnDial.setOnClickListener(v -> {
                Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56912345678"));
                startActivity(dial);
            });
        }

        if (btnGaleria != null) {
            btnGaleria.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        }

        // abrir configuración de Bluetooth
        if (btnBluetooth != null) {
            btnBluetooth.setOnClickListener(v -> {
                Intent btIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(btIntent);
            });
        }

        // ====== Thread ======
        simularTrabajoEnSegundoPlano();
    }

    // Linterna
    private void alternarluz() {
        try {
            luz = !luz;
            camara.setTorchMode(camaraID, luz);
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error al controlar la linterna", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camaraID != null && luz) {
            try {
                camara.setTorchMode(camaraID, false);
                luz = false;
                if (btnLinterna != null) btnLinterna.setText("Encender Linterna");
            } catch (CameraAccessException ignored) {}
        }
    }

    // ====== Abrir cámara frontal directamente ======
    private void abrirCamaraFrontalSeguro() {
        // Acción estándar (abre app de cámara en modo foto)
        Intent frontCam = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

        // Pistas para forzar la frontal (no estándar; algunos OEMs las respetan)
        frontCam.putExtra("android.intent.extras.CAMERA_FACING", 1); // 1 = front
        frontCam.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        frontCam.putExtra("android.intent.extra.CAMERA_FACING", 1);
        frontCam.putExtra("camerafacing", 1);
        frontCam.putExtra("previous_mode", 1);

        if (frontCam.resolveActivity(getPackageManager()) != null) {
            startActivity(frontCam);
            return;
        }

        // Fallback: abre cámara del sistema para capturar
        Intent backCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (backCam.resolveActivity(getPackageManager()) != null) {
            startActivity(backCam);
        } else {
            Toast.makeText(this, "No hay app de cámara disponible", Toast.LENGTH_SHORT).show();
        }
    }

    // ====== Simular trabajo en segundo plano (Thread) ======
    private void simularTrabajoEnSegundoPlano() {
        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            runOnUiThread(() ->
                    Toast.makeText(this, "Tarea en segundo plano lista ✅", Toast.LENGTH_SHORT).show());
        }).start();
    }

    // ===== Menú en HomeActivity =====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_perfil) {
            Intent i = new Intent(this, PerfilActivity.class);
            i.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(i);
            return true;

        } else if (id == R.id.action_web) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com")));
            return true;

        } else if (id == R.id.action_ayuda) {
            startActivity(new Intent(this, AyudaActivity.class));
            return true;

        } else if (id == R.id.action_salir) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


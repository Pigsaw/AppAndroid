package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre;
    private TextView tvCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Referencias UI
        tvCorreo = findViewById(R.id.tvCorreo);
        etNombre = findViewById(R.id.etNombre);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        // Recibir datos del Intent (desde HomeActivity)
        String email = getIntent().getStringExtra("email_usuario");
        if (email != null) {
            tvCorreo.setText(email);
        }

        // Guardar cambios y devolver resultado al HomeActivity
        btnGuardar.setOnClickListener(v -> {
            String nombreEditado = etNombre.getText().toString().trim();
            Intent data = new Intent();
            data.putExtra("nombre_editado", nombreEditado.isEmpty() ? "Usuario" : nombreEditado);
            setResult(RESULT_OK, data);
            finish();
        });
    }
}

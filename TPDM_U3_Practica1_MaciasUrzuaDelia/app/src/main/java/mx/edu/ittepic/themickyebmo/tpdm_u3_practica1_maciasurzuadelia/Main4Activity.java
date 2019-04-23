package mx.edu.ittepic.themickyebmo.tpdm_u3_practica1_maciasurzuadelia;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Main4Activity extends AppCompatActivity {
    EditText nombre,mgb,estreno;
    Button guardar, actualizar, borrar;
    Boolean esRegistro, actualizando;
    String nombreID;
    FirebaseFirestore baseDeDatos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        nombre = findViewById(R.id.nombre);
        mgb = findViewById(R.id.mgb);
        estreno = findViewById(R.id.estreno);
        guardar = findViewById(R.id.guardar);
        actualizar = findViewById(R.id.actualizar);
        borrar = findViewById(R.id.borrar);
        esRegistro = getIntent().getBooleanExtra("esRegistro", true);
        actualizando = false;
        nombreID = getIntent().getStringExtra("nombre");
        baseDeDatos = FirebaseFirestore.getInstance();

        ajustarContenido();

        if (!esRegistro) {
            recuperarPelicula();
            guardar.setText("Cancelar");
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizando) {
                    cambioActualizar(false);
                    recuperarPelicula();
                } else {
                    if (esValido()) {
                        insertar("insertó", "insertar");
                    }
                }
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizando && esValido()) {
                    eliminar(false);
                    insertar("actualizó", "actalizar");
                    cambioActualizar(false);
                } else {
                    cambioActualizar(true);

                }
            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar(true);
            }
        });
    }

    private void cambioActualizar (boolean seQuiereActualizar) {
        if (seQuiereActualizar) {
            guardar.setVisibility(View.VISIBLE);
            actualizar.setText("Aceptar");
            borrar.setVisibility(View.INVISIBLE);
        } else {
            guardar.setVisibility(View.INVISIBLE);
            actualizar.setText("Actualizar");
            borrar.setVisibility(View.VISIBLE);
        }
        actualizando = seQuiereActualizar;

        nombre.setEnabled(seQuiereActualizar);
        mgb.setEnabled(seQuiereActualizar);
        estreno.setEnabled(seQuiereActualizar);
    }

    private void recuperarPelicula () {
        final DocumentReference peliculaonline = baseDeDatos.collection("online").document(nombreID);

       peliculaonline.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot peliculaonline = task.getResult();

                    nombre.setText(nombreID);
                    mgb.setText(peliculaonline.get("mgb").toString());
                    estreno.setText(peliculaonline.get("estreno").toString());
                } else {
                    miniMensaje("Error al recuperar la Pelicula");
                }
            }
        });
    }

    private Map<String, Object> obtenerPelicula() {
        Map<String, Object> nuevaPeli = new HashMap<>();
        nuevaPeli.put("mgb", mgb.getText().toString());
        nuevaPeli.put("estreno", estreno.getText().toString());
        return nuevaPeli;
    }

    private void ajustarContenido() {
        if (esRegistro) {
            actualizar.setVisibility(View.INVISIBLE);
            borrar.setVisibility(View.INVISIBLE);
        } else {
            guardar.setVisibility(View.INVISIBLE);

            nombre.setEnabled(false);
            mgb.setEnabled(false);
            estreno.setEnabled(false);
        }
    }

    private void miniMensaje (String mensaje) {
        Toast.makeText(Main4Activity.this, mensaje, Toast.LENGTH_LONG).show();
    }

    private void insertar(final String m1, final String m2) {
        baseDeDatos.collection("online").document(nombre.getText().toString())
                .set(obtenerPelicula()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                miniMensaje("Se "+ m1 +" correctamente");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                miniMensaje("Error al " + m2);
            }
        });
        finish();
    }

    private void eliminar (final boolean esEliminación) {
        baseDeDatos.collection("online").document(nombreID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (esEliminación) {
                    miniMensaje("Se eliminó correctamente");
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (esEliminación) {
                    miniMensaje("Error al eliminar");
                } else {
                    miniMensaje("Ocurrió un error");
                    finish();
                }
            }
        });
    }

    private boolean esValido () {
        if (nombre.getText().toString().equals("")) {
            miniMensaje("Escribe un nombre de la pelicula");
            return false;
        }



        if (mgb.getText().toString().equals("")) {
            miniMensaje("Escribe un tamaño");
            return false;
        }
        if (estreno.getText().toString().equals("")) {
            miniMensaje("Escribe una fecha de estreno");
            return false;
        }

        return true;
    }
}

package com.example.app_final;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText et_nombre;
    private ImageView iv_personaje;
    private TextView tv_score;
    private MediaPlayer mp;

    int num_aleatorio = (int) (Math.random() * 10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_nombre = (EditText) findViewById(R.id.et_nombre);
        iv_personaje = (ImageView) findViewById(R.id.iv_personaje);
        tv_score = (TextView) findViewById(R.id.tv_score);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        int id;
        if (num_aleatorio < 3) {//aca puse charmander sin .png
            id = getResources().getIdentifier("squirtle", "mipmap", getPackageName());
            iv_personaje.setImageResource(id);
        } else if (num_aleatorio > 3 && num_aleatorio < 6) {
            id = getResources().getIdentifier("charmander", "mipmap", getPackageName());
            iv_personaje.setImageResource(id);
        } else if (num_aleatorio > 6 && num_aleatorio < 7) {
            id = getResources().getIdentifier("bulbasaur", "mipmap", getPackageName());
            iv_personaje.setImageResource(id);
        } else if (num_aleatorio > 7) {
            id = getResources().getIdentifier("pikachu", "mipmap", getPackageName());
            iv_personaje.setImageResource(id);
        }

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "BD",null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();

        Cursor consulta = BD.rawQuery(
                "SELECT nombre , score FROM puntaje WHERE score = (SELECT max(score) from puntaje)", null);
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);
            tv_score.setText("El record es de " + temp_nombre + " con " + temp_score + " puntos");
            BD.close();//hay que cerrar la base de datos
        }  else BD.close();

        mp = MediaPlayer.create(this,R.raw.theme_song_bits);
        mp.start();
        mp.setLooping(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    //Metodo del boton jugar
    public void Jugar(View view){
        String nombre_jugador = et_nombre.getText().toString();

        if(nombre_jugador.length() != 0){
            mp.stop();
            mp.release();

            Intent intent = new Intent(this,Main2Activity_Nivel1.class);
            intent.putExtra("nombre_jugador",nombre_jugador);
            startActivity(intent);
            //esto me da error
            finish();
        }else{
            Toast.makeText(this, "Ingresa primero tu nombre!!!", Toast.LENGTH_SHORT).show();
            //para abrir el editText automaticamente
            et_nombre.requestFocus();
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_nombre,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    //Metodo para controlar el boton de BACK
    @Override
    public void onBackPressed(){

    }
}

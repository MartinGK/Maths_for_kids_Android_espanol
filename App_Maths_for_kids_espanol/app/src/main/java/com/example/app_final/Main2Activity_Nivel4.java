package com.example.app_final;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity_Nivel4 extends AppCompatActivity {

    private ImageView iv_vidas, iv_numerouno, iv_numerodos, iv_simbolo;
    private TextView tv_nombre_jugador, tv_score;
    private EditText et_respuesta;
    private MediaPlayer mp, mp_bad, mp_good;

    int score, numAleatorio_uno, numAleatorio_dos,resultado, vidas=3;
    String nombre_jugador, string_score, string_vidas;
    String numeros[] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2__nivel4);

        Toast.makeText(this, "Nivel 4 - Sumas y Restas", Toast.LENGTH_SHORT).show();

        iv_vidas = (ImageView)findViewById(R.id.iv_vidas);
        iv_numerouno = (ImageView)findViewById(R.id.iv_numerouno);
        iv_numerodos = (ImageView)findViewById(R.id.iv_numerodos);
        iv_simbolo = (ImageView)findViewById(R.id.iv_simbolo);
        tv_nombre_jugador = (TextView)findViewById(R.id.tv_nombre);
        tv_score = (TextView)findViewById(R.id.tv_score);
        et_respuesta = (EditText)findViewById(R.id.et_respuesta);

        nombre_jugador = getIntent().getStringExtra("nombre_jugador");
        tv_nombre_jugador.setText("Jugador: " + nombre_jugador);

        score = Integer.parseInt(getIntent().getStringExtra("string_score"));
        tv_score.setText("Score: "+score);


        vidas = Integer.parseInt(getIntent().getStringExtra("string_vidas"));
        switch (vidas){
            case 3:
                iv_vidas.setImageResource(R.mipmap.tres_vidas);
                break;
            case 2:
                iv_vidas.setImageResource(R.mipmap.dos_vidas);
                break;
            case 1:
                iv_vidas.setImageResource(R.mipmap.una_vida);
                break;
        }

        //Para mantener el icono...
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mp = MediaPlayer.create(this,R.raw.theme_song_bits);
        mp.start();
        mp.setLooping(true);

        mp_good = MediaPlayer.create(this,R.raw.good);
        mp_bad = MediaPlayer.create(this,R.raw.bad);

        NumAleatorio();
    }

    //Metodo para el boton Comprobar
    public void Comprobar(View view){
        String respuesta = et_respuesta.getText().toString();

        if(respuesta.length()!=0){
            int respuesta_int = Integer.parseInt(respuesta);
            if(resultado==respuesta_int){
                mp_good.start();
                score = score + 3;
                tv_score.setText("Score: "+ score);
                et_respuesta.setText("");
                Toast.makeText(this, "PERFECTO!!", Toast.LENGTH_SHORT).show();
                BaseDeDatos();
            }else{
                mp_bad.start();
                Toast.makeText(this, "Respuesta incorrecta", Toast.LENGTH_SHORT).show();
                vidas--;
                BaseDeDatos();

                switch (vidas){
                    case 2:
                        iv_vidas.setImageResource(R.mipmap.dos_vidas);
                        Toast.makeText(this, "Te quedan 2 oportunidades!!", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        iv_vidas.setImageResource(R.mipmap.una_vida);
                        Toast.makeText(this, "Te quedan 1 oportunidad!!!", Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(this, "Te quedaste sin oportunidades!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent (this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        mp.stop();
                        mp.release();
                        break;
                }
                et_respuesta.setText("");
            }
            NumAleatorio();
        }else{
            Toast.makeText(this, "Escribe una respuesta!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void NumAleatorio(){
        if(score <= 39){
            numAleatorio_uno = (int)(Math.random() * 10);
            numAleatorio_dos = (int)(Math.random() * 10);

            if(numAleatorio_uno>=0 && numAleatorio_uno<=5){
                resultado = numAleatorio_uno - numAleatorio_dos;
                iv_simbolo.setImageResource(R.mipmap.menos);
            }else{
                resultado = numAleatorio_uno + numAleatorio_dos;
                iv_simbolo.setImageResource(R.mipmap.mas);
            }

            if(resultado>=0) {
                for (int i = 0; i < numeros.length; i++) {
                    int id = getResources().getIdentifier(numeros[i], "mipmap", getPackageName());
                    if (numAleatorio_uno == i) {
                        iv_numerouno.setImageResource(id);
                    }
                    if (numAleatorio_dos == i) {
                        iv_numerodos.setImageResource(id);
                    }
                }
            }else{
                NumAleatorio();
            }

        }else{
            Intent intent = new Intent(this,Main2Activity_Nivel5.class);

            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("nombre_jugador",nombre_jugador);
            intent.putExtra("string_score",string_score);
            intent.putExtra("string_vidas", string_vidas);

            startActivity(intent);
            finish();
            mp.stop();
            mp.release();
        }
    }
    //Control del score
    public void BaseDeDatos(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"BD",null,1);
        SQLiteDatabase BD = admin.getWritableDatabase();
        Cursor consulta = BD.rawQuery("SELECT * FROM  puntaje where score = (SELECT max(score) FROM puntaje)",null);

        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);

            int BestScore = Integer.parseInt(temp_score);

            if(score>BestScore){
                ContentValues modificacion = new ContentValues();
                modificacion.put("nombre",nombre_jugador);
                modificacion.put("score",score);
                BD.update("puntaje",modificacion,"score = "+ BestScore, null);
                BD.close();
            }
        }else{
            ContentValues insertar = new ContentValues();
            insertar.put("nombre",nombre_jugador);
            insertar.put("score",score);
            BD.insert("puntaje",null,insertar);
            BD.close();
        }
    }
    //Metodo para controlar el boton de BACK
    @Override
    public void onBackPressed(){

    }
}

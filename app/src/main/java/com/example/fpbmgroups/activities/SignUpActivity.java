package com.example.fpbmgroups.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fpbmgroups.R;
import com.example.fpbmgroups.databinding.ActivitySignInBinding;
import com.example.fpbmgroups.databinding.ActivitySignUpBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.texSignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        });

        binding.submitSignUp.setOnClickListener(v -> {

            String name = binding.inputName.getText().toString();
            if(name.matches("[a-zA-Z]+"))
            {
                binding.erreurName.setVisibility(View.INVISIBLE);
                binding.inputName.setBackgroundResource(R.drawable.background_input);
                String firstname = binding.inputFirstName.getText().toString();
                if(firstname.matches("[a-zA-Z]+"))
                {
                    binding.erreurFirstname.setVisibility(View.INVISIBLE);
                    binding.inputFirstName.setBackgroundResource(R.drawable.background_input);
                    String username = binding.inputEmail.getText().toString();
                    if (username.length() > 10)
                    {
                        binding.erreurUsername.setVisibility(View.INVISIBLE);
                        binding.inputEmail.setBackgroundResource(R.drawable.background_input);
                        String password = binding.inputPassword.getText().toString();
                        if(password.length() > 10)
                        {
                            binding.erreurPassword.setVisibility(View.INVISIBLE);
                            binding.inputPassword.setBackgroundResource(R.drawable.background_input);
                            String confirmpassword = binding.inputConfirmPassword.getText().toString();
                            if (password.equals(confirmpassword))
                            {
                                binding.erreurConfirmpassword.setVisibility(View.INVISIBLE);
                                binding.inputConfirmPassword.setBackgroundResource(R.drawable.background_input);
                                try {
                                    new SignUpTask(this).execute(name, firstname, username, password);
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(this,"erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                            else
                            {
                                binding.erreurConfirmpassword.setText("le mot de passe n'est pas le même");
                                binding.erreurConfirmpassword.setVisibility(View.VISIBLE);
                                binding.inputConfirmPassword.setBackgroundResource(R.drawable.error_input_shadow);
                            }
                        }
                        else
                        {
                            binding.erreurPassword.setText("entrez plus de 10 caractères");
                            binding.erreurPassword.setVisibility(View.VISIBLE);
                            binding.inputPassword.setBackgroundResource(R.drawable.error_input_shadow);
                        }

                    }else
                    {
                        binding.erreurUsername.setText("utilisez une adresse email correcte");
                        binding.erreurUsername.setVisibility(View.VISIBLE);
                        binding.inputEmail.setBackgroundResource(R.drawable.error_input_shadow);
                    }
                }
                else
                {
                    binding.erreurFirstname.setText("utilisez juste des alphabets");
                    binding.erreurFirstname.setVisibility(View.VISIBLE);
                    binding.inputFirstName.setBackgroundResource(R.drawable.error_input_shadow);
                }
            }
            else
            {
                binding.erreurName.setText("utilisez juste des alphabets");
                binding.erreurName.setVisibility(View.VISIBLE);
                binding.inputName.setBackgroundResource(R.drawable.error_input_shadow);
            }


        });
    }




    public static class SignUpTask extends AsyncTask<String, Void, String> {

        AlertDialog dialog;
        @SuppressLint("StaticFieldLeak")
        Context context;
        String server_host = "192.168.43.101:70" ;
        String server_tache_file = "signup.php" ;

        String response = "ok!" ;
        String username = "";


        public SignUpTask(Context context)
        {
            this.context = context;
        }


        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(context).create();
            dialog.setTitle("Sign Up Status");
        }

        @Override
        protected void onPostExecute(String s)
        {



            if (s.equals("done"))
            {
                dialog.setMessage("vos données ont étées enregistrées avec succé");
                dialog.show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        goToSignIN();
                    }
                },
                        1500);

            }
            else
            {
                if (s.equals("exists"))
                {
                    Toast.makeText(this.context.getApplicationContext(), "vous avez déjas un compte ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (s.equals("non"))
                    {
                        dialog.setMessage("les données ne sont pas enregistréres");
                        dialog.show();
                    }
                }
            }


        }

        public void goToSignIN()
        {
            Intent intent = new Intent(this.context.getApplicationContext(), SignInActivity.class);
            context.startActivity(intent);
        }



        @Override
        protected String doInBackground(String... voids) {

            StringBuilder result = new StringBuilder();
            String name = voids[0];
            String firstname = voids[1];
            String username = voids[2];
            String password = voids[3];

            String server_link = "http://" + server_host +"/FPBMGroups/" + server_tache_file ;


            try {
                URL url = new URL(server_link);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("name","UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&&" + URLEncoder.encode("firstname","UTF-8") + "=" + URLEncoder.encode(firstname, "UTF-8") + "&&" + URLEncoder.encode("username","UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&&" + URLEncoder.encode("password","UTF-8")  + "=" + URLEncoder.encode(password, "UTF-8");

                writer.write(data);
                writer.flush();
                writer.close();
                ops.close();

                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.ISO_8859_1));
                String line = "" ;
                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
                reader.close();
                ips.close();
                http.disconnect();
                return result.toString().trim();

            } catch (Exception e) {
                result = new StringBuilder(Objects.requireNonNull(e.getMessage()));
            }

            /*try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connexion = DriverManager.getConnection("jdbc:mysql://192.168.1.38/dbqcm", "nanobot", "Nano.1999");
                Statement statement = connexion.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM formulaire ");

                while (result.next())
                {
                    records += result.getString(1);
                }

            }
            catch (Exception e)
            {
                error = e.getMessage();
            }*/


            return result.toString();
        }


    }


}
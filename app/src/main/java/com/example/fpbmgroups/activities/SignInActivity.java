package com.example.fpbmgroups.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//import com.example.fpbmgroups.R;
import com.example.fpbmgroups.R;
import com.example.fpbmgroups.databinding.ActivitySignInBinding;
import com.example.fpbmgroups.localdatabase.GroupsLocalDB;
//import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private GroupsLocalDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new GroupsLocalDB(this);

        if (db.existanceOfItem("username") && db.existanceOfItem("userpassword"))
        {
          //  Intent intent = new Intent(this.getApplicationContext(),HomeActivity.class);
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners()
    {
        binding.texCreateNewAccount.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });

        binding.submitSignIn.setOnClickListener(v -> {

            String username = binding.inputEmail.getText().toString();

            Pattern pattern = Pattern.compile("[A-Za-z0-9._-]+@[A-Za-z0-9._-]+.[A-Za-z]");
            Matcher mat = pattern.matcher(username);
            if (mat.matches() && username.length() > 10)
            {
                binding.inputEmail.setBackgroundResource(R.drawable.background_input);
                binding.erreurUsername.setVisibility(View.INVISIBLE);
                String password = binding.inputPassword.getText().toString();
                if (password.length() > 10)
                {
                    binding.inputPassword.setBackgroundResource(R.drawable.background_input);
                    binding.erreurPassword.setVisibility(View.INVISIBLE);
                    try {
                        new SignInTask(this).execute(username, password);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, "erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    binding.inputPassword.setBackgroundResource(R.drawable.error_input_shadow);
                    binding.erreurPassword.setText("utilisez 10 caractères au minimum");
                    binding.erreurPassword.setVisibility(View.VISIBLE);
                }

            }
            else
            {
                binding.inputEmail.setBackgroundResource(R.drawable.error_input_shadow);
                binding.erreurUsername.setText("entrez une adresse email valide");
                binding.erreurUsername.setVisibility(View.VISIBLE);
            }


            //binding.inputEmail.setText(text);
        });
    }


    public static class SignInTask extends AsyncTask<String, Void, String> {

        AlertDialog dialog;
        @SuppressLint("StaticFieldLeak")
        Context context;

        public SignInTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new AlertDialog.Builder(context).create();
            dialog.setTitle("Login Status");
        }

        @Override
        protected void onPostExecute(String s) {
            Pattern pattern = Pattern.compile("failed to connect", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(s);
            boolean matchFound = matcher.find();
            if (matchFound)
            {
                dialog.setMessage("Vérifiez votre connection et réssayez");
                dialog.show();
            }
            else
            {
                dialog.setMessage(s);
                dialog.show();
            }



        }

        @Override
        protected String doInBackground(String... voids) {

            StringBuilder result = new StringBuilder();
            String username = voids[0];
            String password = voids[1];
            String server_host = "192.168.43.101:70" ;
            String server_tache_file = "login.php" ;


            String server_link = "http://" + server_host + "/FPBMGroups/" + server_tache_file;


            try {
                URL url = new URL(server_link);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops = http.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, StandardCharsets.UTF_8));
                String data = URLEncoder.encode("username","UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&&" + URLEncoder.encode("password","UTF-8")  + "=" + URLEncoder.encode(password, "UTF-8");

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
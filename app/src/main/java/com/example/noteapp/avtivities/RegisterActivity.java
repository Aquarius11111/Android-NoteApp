package com.example.noteapp.avtivities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapp.R;
import com.example.noteapp.database.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etPasswordConfirm;
    private Button btnRegister;
    private DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = DBHelper.getInstance(this);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String passwordConfirm = etPasswordConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = dbHelper.registerUser(username, password);
            if (success) {
                Toast.makeText(RegisterActivity.this, "注册成功，请返回登录", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

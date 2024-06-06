package com.example.bufrie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.bufrie.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
    public FragmentStateAdapter adapter;
    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.include);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        adapter = new MyAdapter(this);
        binding.page.setAdapter(adapter);
        binding.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.page.setCurrentItem(1);
            }
        });
        binding.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.page.setCurrentItem(0);
            }
        });
        binding.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.page.setCurrentItem(2);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.aboutAuthor){
            showAboutAuthorDialog();
        }
        else if (id == R.id.aboutProg){
            showAboutProgDialog();
        }
        else if (id == R.id.help){
            showInstructionsDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showAboutAuthorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Об авторе");
        builder.setIcon(R.drawable.my_ava);
        String developerInfo = "Разработчик: Боренко М.Д.\n" +
                "Группа: ИКБО-03-22\n" +
                "Год разработки приложения: 2024\n" +
                "Почта для связи:\nborenko.maya@mail.ru";

        builder.setMessage(developerInfo);
        builder.setPositiveButton("ОК", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAboutProgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("О программе");
        builder.setMessage("Год разработки: 2024");
        builder.setPositiveButton("ОК", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInstructionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Инструкция пользователю");
        builder.setMessage("Покупателю:\n1. Нажмите на объявление\n"+"" +
                "2. Добавьте в понравившиеся, чтобы вернуться потом\n"+
                "3. напишите по указанным контактам\nПродавцу:\n"+
                "1. Зайдите в личный кабинет\n2. Нажмите кнопку создания объявления\n"+
                "3. Добавьте в объявление данные и фото\n4. Сохраните\n"+
                "5. Для редактирования уже созданного объявления нажмите кнопку в правом верхнем углу\n6. Не забудьте сохранить!");
        builder.setPositiveButton("ОК", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
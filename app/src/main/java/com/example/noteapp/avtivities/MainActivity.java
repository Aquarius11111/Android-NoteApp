package com.example.noteapp.avtivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.noteapp.R;
import com.example.noteapp.fragments.MyNotesFragment;
import com.example.noteapp.fragments.PublishFragment;
import com.example.noteapp.fragments.RecommendFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private int userId;
    private String username;

    private BottomNavigationView bottomNavigationView;

    private RecommendFragment recommendFragment;
    private PublishFragment publishFragment;
    private MyNotesFragment myNotesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取登录传来的用户信息
        userId = getIntent().getIntExtra("userId", -1);
        username = getIntent().getStringExtra("username");

        if (userId == -1) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        recommendFragment = RecommendFragment.newInstance(userId);
        publishFragment = PublishFragment.newInstance(userId);
        myNotesFragment = MyNotesFragment.newInstance(userId);

        // 根据传入的参数决定默认显示的页面
        String defaultPage = getIntent().getStringExtra("defaultPage");
        if (defaultPage != null) {
            switch (defaultPage) {
                case "recommend":
                    loadFragment(recommendFragment);
                    bottomNavigationView.setSelectedItemId(R.id.nav_recommend);
                    break;
                case "publish":
                    loadFragment(publishFragment);
                    bottomNavigationView.setSelectedItemId(R.id.nav_publish);
                    break;
                case "my":
                    loadFragment(myNotesFragment);
                    bottomNavigationView.setSelectedItemId(R.id.nav_my);
                    break;
                default:
                    loadFragment(recommendFragment);
                    bottomNavigationView.setSelectedItemId(R.id.nav_recommend);
                    break;
            }
        } else {
            // 默认显示推荐页
            loadFragment(recommendFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_recommend);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_recommend) {
                loadFragment(recommendFragment);
                return true;
            } else if (itemId == R.id.nav_publish) {
                loadFragment(publishFragment);
                return true;
            } else if (itemId == R.id.nav_my) {
                loadFragment(myNotesFragment);
                return true;
            }
            return false;
        });
    }

    public void showPage(String page) {
        switch (page) {
            case "recommend":
                loadFragment(recommendFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_recommend);
                break;
            case "publish":
                loadFragment(publishFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_publish);
                break;
            case "my":
                loadFragment(myNotesFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_my);
                break;
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            // 刷新当前显示的界面
//            int selectedItemId = bottomNavigationView.getSelectedItemId();
//            if (selectedItemId == R.id.nav_recommend) {
//                recommendFragment.searchNotes(null);
//            } else if (selectedItemId == R.id.nav_my) {
//                myNotesFragment.searchMyNotes(null);
//            }
//        }
//    }

//    private void loadFragment(Fragment fragment) {
//        if (fragment instanceof RecommendFragment) {
//            ((RecommendFragment) fragment).searchNotes(null);
//        } else if (fragment instanceof MyNotesFragment) {
//            ((MyNotesFragment) fragment).searchMyNotes(null);
//        }
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commit();
//    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}

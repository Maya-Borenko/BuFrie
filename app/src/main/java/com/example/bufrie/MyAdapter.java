package com.example.bufrie;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyAdapter extends FragmentStateAdapter {
    public MyAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddFragment();
            case 1:
                return new FavFragment();
            case 2:
                return new PersonFragment(new User("Me", true));
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

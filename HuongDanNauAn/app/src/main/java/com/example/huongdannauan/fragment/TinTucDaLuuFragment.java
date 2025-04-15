package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.AllBlogAdapter;
import com.example.huongdannauan.adapter.AllRecipeAdapter;
import com.example.huongdannauan.model.Blog;
import com.example.huongdannauan.model.Recipe;
import com.example.huongdannauan.model.TrangThai;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TinTucDaLuuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TinTucDaLuuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private AllBlogAdapter adapterAllRecipe;
    private List<Blog> recipeList = new ArrayList<>();
    private DatabaseReference databaseReference;
    ProgressBar progressBar;

    public TinTucDaLuuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TinTucDaLuuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TinTucDaLuuFragment newInstance(String param1, String param2) {
        TinTucDaLuuFragment fragment = new TinTucDaLuuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tin_tuc_da_luu, container, false);

        recyclerView = view.findViewById(R.id.lvBlog);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference("blog");
        loadRecipesFromFirebase();

        return view;
    }
    private void loadRecipesFromFirebase() {
        // Kiểm tra đăng nhập
        if(TrangThai.userEmail==null || TrangThai.userEmail.isEmpty()){
            DangNhapFragment loginFragment = new DangNhapFragment();
            openAccountFragment(loginFragment);
            return;
        }

        // Adapter All Mon an
        adapterAllRecipe = new AllBlogAdapter(getContext(), recipeList, recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        recyclerView.setAdapter(adapterAllRecipe);

        //Lấy id món ăn yêu thích và danh sách món ăn
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");
        Query query = userRef.orderByChild("email").equalTo(TrangThai.userEmail);
        Log.e("BAOBAOSHOP email", TrangThai.userEmail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipeList.clear();
                if (dataSnapshot.exists()) {
                    // Lặp qua các kết quả
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (userSnapshot.child("tinTucDaLuu").exists()){
                            String monAnDaLuu = userSnapshot.child("tinTucDaLuu").getValue(String.class);
                            Log.e("BAOBAOSHOP tinTucDaLuu", TrangThai.userEmail);
                            // Kiểm tra nếu "tinTucDaLuu" không null
                            if (monAnDaLuu != null && !monAnDaLuu.isEmpty()) {
                                // Chuyển đổi "monAnDaLuu" thành danh sách các ID
                                List<String> listMonAn = new ArrayList<>(Arrays.asList(monAnDaLuu.split(",")));

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (String recipeID : listMonAn) {
                                            if (dataSnapshot.hasChild(recipeID)) {
                                                Blog recipe = dataSnapshot.child(recipeID).getValue(Blog.class);
                                                recipeList.add(recipe);
                                            }
                                        }
                                        adapterAllRecipe.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("FirebaseError", "Error loading recipes: " + databaseError.getMessage());
                                    }
                                });
                            }
                        }

                    }
                    adapterAllRecipe.notifyDataSetChanged();
                } else {
                    System.out.println("No user found with email: " + TrangThai.userEmail);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });

    }
    private void openRecipeDetailFragment(int recipeId) {
        BlogDetailFragment chiTietMonAnFragment = BlogDetailFragment.newInstance(String.valueOf(recipeId), "");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chiTietMonAnFragment)
                .addToBackStack(null)
                .commit();
    }
    private void openAccountFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        if (getActivity() != null) {
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
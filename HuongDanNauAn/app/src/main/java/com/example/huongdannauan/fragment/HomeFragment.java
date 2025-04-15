package com.example.huongdannauan.fragment;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.BlogAdapter;
import com.example.huongdannauan.adapter.DishTypeAdapter;
import com.example.huongdannauan.adapter.RecipeAdapter;
import com.example.huongdannauan.model.Blog;
import com.example.huongdannauan.model.DishType;
import com.example.huongdannauan.model.FoodCategory;
import com.example.huongdannauan.model.Recipe;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private DatabaseReference databaseReference1, databaseReference2;
    RecyclerView recyclerView1;
    private RecipeAdapter recipeAdapter1;
    private List<Recipe> recipeList1;
    RecyclerView recyclerView2;
    private RecipeAdapter recipeAdapter2;
    private List<Recipe> recipeList2;
    RecyclerView recyclerViewBlog;
    private BlogAdapter blogAdapter;
    private List<Blog> blogList;
    RecyclerView recyclerViewLoai;
    private DishTypeAdapter recipeAdapterLoai;
    private List<DishType> recipeListLoai;
    TextView txtXemAll, txtXemAllVN, txtSearch, txtXemAllLoai, txtXemAllBlog;
    Button btnSeach;
    ProgressBar progressBar1, progressBar2, progressBarLoai, progressBar3;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Ẩn bàn phím

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Add controls
        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar3 = view.findViewById(R.id.progressBar3);
        progressBarLoai = view.findViewById(R.id.progressBarLoai);
        recyclerView1 = view.findViewById(R.id.recyclerViewHome1);
        recyclerView2 = view.findViewById(R.id.recyclerViewHome2);
        recyclerViewLoai = view.findViewById(R.id.recyclerViewLoai);
        txtXemAll = view.findViewById(R.id.txtXemAll);
        txtXemAllVN = view.findViewById(R.id.txtXemAllVN);
        txtXemAllLoai = view.findViewById(R.id.txtXemAllLoai);
        txtSearch = view.findViewById(R.id.search_edit_text);
        btnSeach = view.findViewById(R.id.search_button);
        recyclerViewBlog = view.findViewById(R.id.recyclerViewHomeBlog);
        txtXemAllBlog = view.findViewById(R.id.txtXemAllBlog);


        eventClick();

        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);
        progressBar3.setVisibility(View.VISIBLE);
        progressBarLoai.setVisibility(View.VISIBLE);

        // Cài đặt RecyclerView  - Món ăn Nổi bật,..
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewBlog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewLoai.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recipeList1 = new ArrayList<>();
        recipeList2 = new ArrayList<>();
        recipeListLoai = new ArrayList<>();
        blogList = new ArrayList<>();
        recipeAdapter1 = new RecipeAdapter(recipeList1, getContext(), recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        recipeAdapter2 = new RecipeAdapter(recipeList2, getContext(), recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        recipeAdapterLoai = new DishTypeAdapter(recipeListLoai, getContext(), dishTypeName -> {
            openAllRecipeFragment(dishTypeName);
        });
        blogAdapter = new BlogAdapter(blogList, getContext(), blogId -> {
            openBlogDetailFragment(blogId);
        });
        recyclerView1.setAdapter(recipeAdapter1);
        recyclerView2.setAdapter(recipeAdapter2);
        recyclerViewLoai.setAdapter(recipeAdapterLoai);
        recyclerViewBlog.setAdapter(blogAdapter);

        loadFirebase();


        return view;
    }

    // Phương thức mở RecipeDetailFragment và truyền recipeId
    private void openRecipeDetailFragment(int recipeId) {
        ChiTietMonAnFragment chiTietMonAnFragment = ChiTietMonAnFragment.newInstance(String.valueOf(recipeId), "");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chiTietMonAnFragment)
                .addToBackStack(null)
                .commit();
    }
    private void openBlogDetailFragment(int blogId) {
        BlogDetailFragment blogDetailFragment = BlogDetailFragment.newInstance(String.valueOf(blogId), "");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, blogDetailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openAllRecipeFragment(String dishTypeName) {
        AllRecipeFragment allRecipeFragment = AllRecipeFragment.newInstance(dishTypeName, "");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, allRecipeFragment)
                .addToBackStack(null)
                .commit();
    }

    void loadFirebase(){
        // Đổ dữ liệu từ Firebase
        databaseReference1 = FirebaseDatabase.getInstance().getReference("recipes");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList1.clear();
                recipeList2.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                        if (recipeList1.size()<10 && recipe != null) {
                            recipeList1.add(recipe);
                        }
                        if (recipeList2.size()<10 && recipe != null && recipe.getCuisines() != null && recipe.getCuisines().contains("Việt Nam")) {
                            recipeList2.add(recipe);
                        }
                        if (recipeList1.size()==10 && recipeList2.size()==10){
                            break;
                        }
                        progressBar1.setVisibility(View.GONE);
                        progressBar2.setVisibility(View.GONE);
                        progressBarLoai.setVisibility(View.GONE);
                    } catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error deserializing data", e);
                        progressBar1.setVisibility(View.GONE);
                        progressBar2.setVisibility(View.GONE);
                        progressBarLoai.setVisibility(View.GONE);
                    }
                }

                recipeListLoai.add(new DishType("https://cdn0.iconfinder.com/data/icons/foods-5/64/_Soup-512.png", "Canh"));
                recipeListLoai.add(new DishType("https://cdn2.iconfinder.com/data/icons/bakery-color/200/05-512.png", "Bữa trưa"));
                recipeListLoai.add(new DishType("https://cdn2.iconfinder.com/data/icons/a-collection-of-virtual-gifts/100/thanksgiving_dinner-512.png", "Bữa tối"));
                recipeListLoai.add(new DishType("https://cdn0.iconfinder.com/data/icons/bakery-10/512/Cake-512.png", "Khai vị"));
                recipeListLoai.add(new DishType("https://cdn3.iconfinder.com/data/icons/street-food-and-food-trucker-1/64/dessert-pastry-baked-doughnut-512.png", "Đồ ăn vặt"));

                // Cập nhật Adapter
                recipeAdapter1.notifyDataSetChanged();
                recipeAdapter2.notifyDataSetChanged();
                recipeAdapterLoai.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference2 = FirebaseDatabase.getInstance().getReference("blog");
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blogList.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Blog blog = recipeSnapshot.getValue(Blog.class);
                        blogList.add(blog);
                        progressBar3.setVisibility(View.GONE);
                    } catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error deserializing data", e);
                        progressBar3.setVisibility(View.GONE);
                    }
                    if(blogList.size()==3){
                        break;
                    }
                }

                blogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load blog: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to load blog", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void eventClick(){
        txtXemAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllRecipeFragment allRecipeFragment = new AllRecipeFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, allRecipeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        txtXemAllVN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllRecipeFragment allRecipeFragment = AllRecipeFragment.newInstance("Việt Nam", "");
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, allRecipeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        btnSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllRecipeFragment allRecipeFragment = AllRecipeFragment.newInstance(txtSearch.getText().toString(), "");
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, allRecipeFragment)
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(getContext(), txtSearch.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        txtXemAllLoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodCategoryFragment allRecipeFragment = new FoodCategoryFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, allRecipeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        txtXemAllBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllBlogFragment allRecipeFragment = new AllBlogFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, allRecipeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

}
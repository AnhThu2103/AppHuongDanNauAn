package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.AllBlogAdapter;
import com.example.huongdannauan.adapter.AllRecipeAdapter;
import com.example.huongdannauan.model.Blog;
import com.example.huongdannauan.model.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllBlogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllBlogFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private AllBlogAdapter adapter;
    private List<Blog> blogList = new ArrayList<>();
    private List<Blog> blogListNew = new ArrayList<>();
    private DatabaseReference databaseReference;
    private List<String> datelist;
    private Spinner spinnerDate;
    private ArrayAdapter<String> adapterSpinnerDate;
    private ProgressBar progressBar;
    private TextView txtSearch;
    private Button btnSearch;

    public AllBlogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllBlogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllBlogFragment newInstance(String param1, String param2) {
        AllBlogFragment fragment = new AllBlogFragment();
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
        View view =  inflater.inflate(R.layout.fragment_all_blog, container, false);

        btnSearch = view.findViewById(R.id.btnSearch);
        txtSearch = view.findViewById(R.id.txtSearch);

        recyclerView = view.findViewById(R.id.lvBlogAll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progressBar1);
        spinnerDate = view.findViewById(R.id.spinnerDate);

        databaseReference = FirebaseDatabase.getInstance().getReference("blog");
        loadBlogFromFirebase();

        // Thiết lập spinner Loại
        datelist = new ArrayList<>();
        datelist.add("Mới nhất");
        datelist.add("Cũ nhất");
        adapterSpinnerDate = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, datelist);
        adapterSpinnerDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(adapterSpinnerDate);

        // Thêm listener cho spinner
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    blogListNew.sort((b1, b2) -> b1.getParsedDate().compareTo(b2.getParsedDate()));
                } else {
                    blogListNew.sort((b1, b2) -> b2.getParsedDate().compareTo(b1.getParsedDate()));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có gì được chọn
            }
        });

        addEvent();

        return view;
    }
    void loadBlogFromFirebase(){
        progressBar.setVisibility(View.VISIBLE);
        // Adapter All Mon an
        adapter = new AllBlogAdapter(getContext(), blogListNew, recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        recyclerView.setAdapter(adapter);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                blogList.clear();
                blogListNew.clear();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Blog blog = recipeSnapshot.getValue(Blog.class);
                        blogList.add(blog);
                        blogListNew.add(blog);
                        progressBar.setVisibility(View.GONE);
                    } catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error deserializing data", e);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
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
    void addEvent(){
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blogListNew.clear();
                for (Blog b: blogList) {
                    if(b.getTitle().contains(txtSearch.getText())){
                        blogListNew.add(b);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
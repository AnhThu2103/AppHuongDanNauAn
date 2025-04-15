package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.AllRecipeAdapter;
import com.example.huongdannauan.adapter.RecipeAdapter;
import com.example.huongdannauan.model.Ingredient;
import com.example.huongdannauan.model.Recipe;
import com.example.huongdannauan.model.TrangThai;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllRecipeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private AllRecipeAdapter adapterAllRecipe;
    private List<Recipe> recipeList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private List<String> dishTypesList, vungmienList;
    private Spinner spinnerLoai, spinnerVungMien;
    private ArrayAdapter<String> adapterSpinnerLoai, adapterSpinnerVungMien;
    private ProgressBar progressBar1;
    private TextView resultCountText, txtSearch;
    private Button btnXoaLoc, btnSearch;
    private String locLoai="Tất cả loại", locVungMien="Xuất xứ";

    // TODO: Rename and change types of parameters
    private String mParam1;
    int lanSearch=0;
    private String mParam2;

    public AllRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param txtsearch Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllRecipeFragment newInstance(String txtsearch, String param2) {
        AllRecipeFragment fragment = new AllRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, txtsearch);
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
        setRetainInstance(true);


    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_recipe, container, false);

        btnXoaLoc = view.findViewById(R.id.btnXoaLoc);
        btnSearch = view.findViewById(R.id.btnSearch);
        txtSearch = view.findViewById(R.id.txtSearch);

        resultCountText = view.findViewById(R.id.resultCountText);
        resultCountText.setText("");

        recyclerView = view.findViewById(R.id.AllrecipeListView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        progressBar1 = view.findViewById(R.id.progressBar1);
        spinnerLoai = view.findViewById(R.id.dishTypeSpinner);
        spinnerVungMien = view.findViewById(R.id.cuisineSpinner);

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        loadRecipesFromFirebase();

        // Thiết lập spinner Loại
        dishTypesList = new ArrayList<>();
        adapterSpinnerLoai = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dishTypesList);
        adapterSpinnerLoai.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoai.setAdapter(adapterSpinnerLoai);
        // Thiết lập spinner Vung Mien
        vungmienList = new ArrayList<>();
        adapterSpinnerVungMien = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, vungmienList);
        adapterSpinnerVungMien.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVungMien.setAdapter(adapterSpinnerVungMien);

        // Thêm listener cho spinner
        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locLoai = dishTypesList.get(position);
                filterRecipesByType(locLoai, locVungMien);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có gì được chọn
            }
        });
        spinnerVungMien.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locVungMien = vungmienList.get(position);
                filterRecipesByType(locLoai, locVungMien);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì nếu không có gì được chọn
            }
        });

        addEvent();

        return view;
    }
    void addEvent(){
        btnXoaLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerLoai.setSelection(0);
                spinnerVungMien.setSelection(0);
                txtSearch.setText("");
                loadRecipesFromFirebase();
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterKetQuaSearch(txtSearch.getText().toString());
                filterKetQuaSearch(txtSearch.getText().toString());
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
                }
            }
        });
    }
    private void loadRecipesFromFirebase() {
        progressBar1.setVisibility(View.VISIBLE);
        // Adapter All Mon an
        adapterAllRecipe = new AllRecipeAdapter(getContext(), recipeList, recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        recyclerView.setAdapter(adapterAllRecipe);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear();
                Set<String> uniqueDishTypes = new HashSet<>();
                Set<String> uniqueDishVungMien = new HashSet<>();
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    try {
                        Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                        recipeList.add(recipe);
                        if(recipe.getDishTypes()!=null){
                            List<String> loai = recipe.getDishTypes();
                            uniqueDishTypes.addAll(loai);
                        }
                        if(recipe.getCuisines()!=null){
                            List<String> vungmien = recipe.getCuisines();
                            uniqueDishVungMien.addAll(vungmien);
                        }
                        progressBar1.setVisibility(View.GONE);
                    } catch (DatabaseException e) {
                        Log.e("FirebaseError", "Error deserializing data", e);
                        progressBar1.setVisibility(View.GONE);
                    }
                }
                adapterAllRecipe.notifyDataSetChanged();
                resultCountText.setText("Tìm thấy "+recipeList.size()+" kết quả");

                dishTypesList.clear();
                dishTypesList.add("Tất cả loại");
                List<String> sortedDishTypesList = new ArrayList<>(uniqueDishTypes);
                Collections.sort(sortedDishTypesList);
                dishTypesList.addAll(sortedDishTypesList);
                adapterSpinnerLoai.notifyDataSetChanged();

                vungmienList.clear();
                vungmienList.add("Xuất xứ");
                List<String> sortedVungMienList = new ArrayList<>(uniqueDishVungMien);
                Collections.sort(sortedVungMienList);
                vungmienList.addAll(sortedVungMienList);
                adapterSpinnerVungMien.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void filterRecipesByType(String Lloai, String Lvungmien) {
        if(mParam1!=null && !mParam1.isEmpty() && lanSearch<2){
            filterKetQuaSearch(mParam1);
            lanSearch++;
            return;
        }
        List<Recipe> filteredList = new ArrayList<>();
        if(Lloai.equals("Tất cả loại") && Lvungmien.equals("Xuất xứ")){
            filteredList = recipeList;
        } else if(Lloai.equals("Tất cả loại") && !Lvungmien.equals("Xuất xứ")){
            for (Recipe recipe : recipeList) {
                if (recipe.getCuisines() != null && recipe.getCuisines().contains(Lvungmien)) {
                    filteredList.add(recipe);
                }
            }
        }else if(!Lloai.equals("Tất cả loại") && Lvungmien.equals("Xuất xứ")){
            for (Recipe recipe : recipeList) {
                if (recipe.getDishTypes() != null && recipe.getDishTypes().contains(Lloai)) {
                    filteredList.add(recipe);
                }
            }
        } else {
            for (Recipe recipe : recipeList) {
                if (recipe.getDishTypes() != null && recipe.getCuisines() != null) {
                    if (recipe.getDishTypes().contains(Lloai) && recipe.getCuisines().contains(Lvungmien)) {
                        filteredList.add(recipe);
                    }
                }
            }
        }
        if(txtSearch.getText().toString()!=null && !txtSearch.getText().toString().trim().isEmpty()){
            Iterator<Recipe> iterator = filteredList.iterator();
            while (iterator.hasNext()) {
                Recipe recipe = iterator.next();
                if (!recipe.getTitle().equalsIgnoreCase(txtSearch.getText().toString())||
                        !containsIngredient(recipe.getExtendedIngredients(), txtSearch.getText().toString())||
                        !containsString(recipe.getDishTypes(), txtSearch.getText().toString())||
                        !containsString(recipe.getCuisines(), txtSearch.getText().toString())
                ) {
                    iterator.remove();
                }
            }
            Log.e("TEST", txtSearch.getText().toString());
        }
        adapterAllRecipe = new AllRecipeAdapter(getContext(), filteredList, recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        adapterAllRecipe.notifyDataSetChanged();
        recyclerView.setAdapter(adapterAllRecipe);
        resultCountText.setText("Tìm thấy "+filteredList.size()+" kết quả");
    }
    private void filterKetQuaSearch(String txtString) {
        int position = dishTypesList.indexOf(txtString); // Tìm vị trí của "mParam1"
        if (position != -1) {
            spinnerLoai.setSelection(position); // Đặt giá trị mặc định nếu tìm thấy
        } else spinnerLoai.setSelection(0);
        int position1 = vungmienList.indexOf(txtString); // Tìm vị trí của "mParam1"
        if (position1 != -1) {
            spinnerVungMien.setSelection(position1); // Đặt giá trị mặc định nếu tìm thấy
        } else spinnerVungMien.setSelection(0);

        List<Recipe> filteredList = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getDishTypes() != null && recipe.getCuisines() != null) {
                if (recipe.getTitle().toLowerCase().contains(txtString.toLowerCase()) ||
                        containsString(recipe.getCuisines(), txtString) ||
                        containsString(recipe.getDishTypes(), txtString) ||
                        containsIngredient(recipe.getExtendedIngredients(), txtString)
                ) {
                    filteredList.add(recipe);
                }
            }
        }

        adapterAllRecipe = new AllRecipeAdapter(getContext(), filteredList, recipeId -> {
            openRecipeDetailFragment(recipeId);
        });
        adapterAllRecipe.notifyDataSetChanged();
        recyclerView.setAdapter(adapterAllRecipe);
        resultCountText.setText("Tìm thấy "+filteredList.size()+" kết quả");
        mParam1=null;
    }
    private void openRecipeDetailFragment(int recipeId) {
        ChiTietMonAnFragment chiTietMonAnFragment = ChiTietMonAnFragment.newInstance(String.valueOf(recipeId), "");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chiTietMonAnFragment)
                .addToBackStack(null)
                .commit();
    }
    public boolean containsIngredient(List<Ingredient> ingredients, String ingredientName) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getName().equalsIgnoreCase(ingredientName)) { // So sánh tên nguyên liệu
                return true;
            }
        }
        return false;
    }
    public boolean containsString(List<String> ingredients, String stringname) {
        for (String ingredient : ingredients) {
            if (ingredient.equalsIgnoreCase(stringname)) { // So sánh tên nguyên liệu
                return true;
            }
        }
        return false;
    }
}
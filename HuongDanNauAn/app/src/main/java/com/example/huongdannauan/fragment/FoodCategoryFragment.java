package com.example.huongdannauan.fragment;

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

import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.DishTypeAdapter;
import com.example.huongdannauan.adapter.FoodCategoryAdapter;
import com.example.huongdannauan.adapter.FoodCategoryCountryAdapter;
import com.example.huongdannauan.model.FoodCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodCategoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView, recyclerViewCountry;
    private FoodCategoryAdapter adapter;
    private List<FoodCategory> foodCategoryList;
    private FoodCategoryCountryAdapter adapterCountry;
    private List<FoodCategory> foodCategoryListCountry;

    public FoodCategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoodCategoryFragment newInstance(String param1, String param2) {
        FoodCategoryFragment fragment = new FoodCategoryFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFoodCategory);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewCountry = view.findViewById(R.id.recyclerViewFoodCategoryCountry);
        recyclerViewCountry.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));



        // Tạo danh sách món ăn
        foodCategoryList = new ArrayList<>();
        adapter = new FoodCategoryAdapter(foodCategoryList, dishTypeName -> {
            openAllRecipeFragment(dishTypeName);
        });
        recyclerView.setAdapter(adapter);

        foodCategoryListCountry = new ArrayList<>();
        foodCategoryListCountry.add(new FoodCategory("Anh", "https://cdn0.iconfinder.com/data/icons/flags-of-the-world-2/128/england-3-512.png"));
        foodCategoryListCountry.add(new FoodCategory("Hy Lạp", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5c/Flag_of_Greece.svg/1200px-Flag_of_Greece.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Hàn Quốc", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQp_B91RjPAjtDAybJBoYMynjlP6XOl3mzHPg&s"));
        foodCategoryListCountry.add(new FoodCategory("Ireland", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Flag_of_Ireland.svg/800px-Flag_of_Ireland.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Mỹ", "https://eurotravel.com.vn/wp-content/uploads/2023/05/quoc-ky-dau-tien-cua-nuoc-my-voi-13-sao-dai-dien-cho-13-bang-ngay-so-khai.png"));
        foodCategoryListCountry.add(new FoodCategory("Nhật Bản", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Flag_of_Japan.svg/225px-Flag_of_Japan.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Pháp", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/Flag_of_France.svg/250px-Flag_of_France.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Scotland", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Flag_of_Scotland.svg/2560px-Flag_of_Scotland.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Thái", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Flag_of_Thailand.svg/1200px-Flag_of_Thailand.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Tây Ban Nha", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Bandera_de_España.svg/800px-Bandera_de_España.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Việt Nam", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/21/Flag_of_Vietnam.svg/800px-Flag_of_Vietnam.svg.png"));
        foodCategoryListCountry.add(new FoodCategory("Ý", "https://m.media-amazon.com/images/I/51IM7P0njOL._AC_UF894,1000_QL80_.jpg"));
        adapterCountry = new FoodCategoryCountryAdapter(foodCategoryListCountry, dishTypeName -> {
            openAllRecipeFragment(dishTypeName);
        });
        recyclerViewCountry.setAdapter(adapterCountry);

        // Gọi hàm lấy dữ liệu từ Firebase
        fetchDishTypesFromFirebase();

        return view;
    }
    private void openAllRecipeFragment(String dishTypeName) {
        AllRecipeFragment allRecipeFragment = AllRecipeFragment.newInstance(dishTypeName, "");
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, allRecipeFragment)
                .addToBackStack(null)
                .commit();
    }
    private void fetchDishTypesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("recipes");
        Map<String, String> uniqueDishTypes = new HashMap<>(); // Key: DishType, Value: Image URL

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    // Lấy danh sách dishTypes và image
                    List<String> dishTypes = (List<String>) recipeSnapshot.child("dishTypes").getValue();
                    String image = recipeSnapshot.child("image").getValue(String.class);

                    if (dishTypes != null && image != null) {
                        for (String dishType : dishTypes) {
                            // Chỉ thêm nếu dishType chưa tồn tại
                            if (!uniqueDishTypes.containsKey(dishType)) {
                                uniqueDishTypes.put(dishType, image);
                            }
                        }
                    }
                }

                // Cập nhật UI
                updateUIWithDishTypes(uniqueDishTypes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }
    private void updateUIWithDishTypes(Map<String, String> uniqueDishTypes) {
        foodCategoryList.clear();

        for (Map.Entry<String, String> entry : uniqueDishTypes.entrySet()) {
            foodCategoryList.add(new FoodCategory(entry.getKey(), entry.getValue()));
        }

        adapter.notifyDataSetChanged();
    }


}
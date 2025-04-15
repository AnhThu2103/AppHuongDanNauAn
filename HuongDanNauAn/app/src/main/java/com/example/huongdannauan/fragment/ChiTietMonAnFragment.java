package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.Interface.OnSentimentResult;
import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.CommentAdapter;
import com.example.huongdannauan.model.Comment;
import com.example.huongdannauan.model.Ingredient;
import com.example.huongdannauan.model.Instruction;
import com.example.huongdannauan.model.Recipe;
import com.example.huongdannauan.model.Step;
import com.example.huongdannauan.model.TrangThai;
import com.example.huongdannauan.model.User;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChiTietMonAnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChiTietMonAnFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String ID_MONAN = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Recipe recipe;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private ImageView imageDish, imgLove;
    private TextView titleDish, summaryDish, preparationMinutes, cookingMinutes, healthScore, cuisines;
    private LinearLayout ingredientsContainer, loaimonanContainer, huongdanNauContainer;
    private RecyclerView recyclerViewComment;
    private CommentAdapter adapter;
    private EditText edittextComment;
    protected Button btnSend;
    List<Comment> comments;
    ProgressBar progressBar1;
    ProgressBar progressBar2;

    public ChiTietMonAnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idmonan Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChiTietMonAnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChiTietMonAnFragment newInstance(String idmonan, String param2) {
        ChiTietMonAnFragment fragment = new ChiTietMonAnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idmonan);
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
            ID_MONAN = mParam1;
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chi_tiet_mon_an, container, false);
        // Ánh xạ các view
        imageDish = view.findViewById(R.id.imageDish);
        titleDish = view.findViewById(R.id.titleDish);
        summaryDish = view.findViewById(R.id.summaryDish);
        preparationMinutes = view.findViewById(R.id.preparationMinutes);
        cookingMinutes = view.findViewById(R.id.cookingMinutes);
        healthScore = view.findViewById(R.id.healthScore);
        cuisines = view.findViewById(R.id.cuisines);
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        loaimonanContainer = view.findViewById(R.id.LoaiMonAnContainer);
        huongdanNauContainer = view.findViewById(R.id.huongDanNauAnContainer);
        recyclerViewComment = view.findViewById(R.id.commentRecyclerView);
        edittextComment = view.findViewById(R.id.editTextComment);
        btnSend = view.findViewById(R.id.buttonSend);
        imgLove = view.findViewById(R.id.imgLove);

        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.VISIBLE);

        addEvent();
        luuLichSuXem();

        // Tham chiếu tới Firebase Database
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("recipes").child(mParam1);

        // Lấy dữ liệu món ăn từ Firebase
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Chuyển đổi dữ liệu Firebase thành đối tượng Recipe
                recipe = snapshot.getValue(Recipe.class);

                // Kiểm tra và cập nhật giao diện nếu recipe không null
                if (recipe != null) {
                    updateUI(recipe);
                }

                progressBar1.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeDetailActivity", "Lỗi khi lấy dữ liệu", error.toException());
                progressBar1.setVisibility(View.GONE);
            }
        });
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("comments").child(mParam1);

        // Lấy dữ liệu món ăn từ Firebase
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments = new ArrayList<>();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                if(comments.size()>0) updateComment(comments);
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeDetailActivity", "Lỗi khi lấy dữ liệu", error.toException());
                progressBar2.setVisibility(View.GONE);
            }
        });

        // Lấy trang thái yêu thích
        if(!TrangThai.userEmail.isEmpty()) {
            getUserByEmail();
        }

        return view;
    }
    private void updateUI(Recipe recipe) {
        Glide.with(this).load(recipe.getImage()).into(imageDish);
        titleDish.setText(recipe.getTitle());
        summaryDish.setText(recipe.getSummary());
        preparationMinutes.setText(String.valueOf(recipe.getPreparationMinutes()==0?30:recipe.getPreparationMinutes()) + " phút");
        cookingMinutes.setText(String.valueOf(recipe.getCookingMinutes()==0?30:recipe.getCookingMinutes()) + " phút");
        healthScore.setText(recipe.getHealthScore()+"");

        cuisines.setText(TextUtils.join(", ", recipe.getCuisines()));


        // Thêm Loai mon an
        for (String loaimonan : recipe.getDishTypes()) {
            TextView loaiView = new TextView(getContext());
            loaiView.setText("- " + loaimonan);
            loaiView.setTextSize(18);
            loaimonanContainer.addView(loaiView);
        }

        // Thêm nguyên liệu
        for (Ingredient ingredient : recipe.getExtendedIngredients()) {
            TextView ingredientView = new TextView(getContext());
            ingredientView.setText("• " + ingredient.getName());
            ingredientView.setTextSize(18);
            ingredientsContainer.addView(ingredientView);
        }

        // Thêm huong dan nau an
        if(recipe.getAnalyzedInstructions()!=null){
            for (Instruction instruction : recipe.getAnalyzedInstructions()) {
                if(!instruction.getName().isEmpty()){
                    TextView ingredientView = new TextView(getContext());
                    ingredientView.setText("• "+instruction.getName() + ": ");
                    ingredientView.setTextSize(18);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ingredientView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                    }
                    ingredientView.setTextColor(Color.rgb(0, 153, 0));
                    huongdanNauContainer.addView(ingredientView);
                }
                if(instruction.getSteps()!=null){
                    for(Step step : instruction.getSteps()){
                        TextView ingredientView = new TextView(getContext());
                        ingredientView.setText("- Bước "+step.getNumber() + ": " + step.getStep());
                        ingredientView.setTextSize(18);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ingredientView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                        }
                        huongdanNauContainer.addView(ingredientView);
                    }
                }
            }
        }
    }
    private void updateComment(List<Comment> commentList) {
        if(commentList!= null){
            recyclerViewComment.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CommentAdapter(commentList, getContext(), requireActivity().getSupportFragmentManager());
            recyclerViewComment.setAdapter(adapter);
        }
    }
    void addEvent(){
        loadComments();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(TrangThai.userEmail.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Đăng nhập");
                    builder.setMessage("Đăng nhập để bình luận.");

                    // Nút xác nhận
                    builder.setPositiveButton("Đồng ý", (dialog, which) -> {

                        // Điều hướng từ Fragment Món ăn đến Fragment Đăng nhập
                        Bundle args = new Bundle();
                        args.putString("return_fragment", "ChiTietMonAnFragment");
                        args.putString("idmonan", mParam1);
                        DangNhapFragment loginFragment = new DangNhapFragment();
                        loginFragment.setArguments(args);

                        openAccountFragment(loginFragment);
                    });

                    // Nút hủy
                    builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

                    // Hiển thị AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference commentsRef = database.getReference("comments/"+mParam1).push(); // id mon an

                    // Tạo dữ liệu cho comment mới
                    Map<String, Object> newComment = new HashMap<>();
                    newComment.put("commentId", commentsRef.getKey());
                    newComment.put("content", edittextComment.getText().toString());
                    newComment.put("date", TrangThai.getCurrentDateString());
                    newComment.put("likes", 0);
                    newComment.put("userEmail", TrangThai.currentUser.getName());
                    newComment.put("replies", new ArrayList<>()); // Không có phản hồi ban đầu

                    // Thêm comment mới vào danh sách
                    commentsRef.setValue(newComment)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    edittextComment.setText("");
                                    Toast.makeText(getContext(), "Thêm comment thành công", Toast.LENGTH_SHORT).show();

                                    // Xóa focus khỏi EditText và ẩn bàn phím
                                    edittextComment.clearFocus();
                                    // Lấy InputMethodManager từ Context
                                    InputMethodManager imm = requireContext().getSystemService(InputMethodManager.class);
                                    imm.hideSoftInputFromWindow(edittextComment.getWindowToken(), 0);

                                    updateComment(comments);
                                    loadComments();

                                } else {
                                    Toast.makeText(getContext(), "Lỗi khi thêm comment:"+ task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        imgLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TrangThai.userEmail.isEmpty()) {
                    luuMonAn();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Đăng nhập");
                    builder.setMessage("Đăng nhập để lưu món ăn yêu thích.");

                    // Nút xác nhận
                    builder.setPositiveButton("Đồng ý", (dialog, which) -> {

                        // Điều hướng từ Fragment Món ăn đến Fragment Đăng nhập
                        Bundle args = new Bundle();
                        args.putString("return_fragment", "ChiTietMonAnFragment");
                        args.putString("idmonan", mParam1);
                        DangNhapFragment loginFragment = new DangNhapFragment();
                        loginFragment.setArguments(args);

                        openAccountFragment(loginFragment);
                    });

                    // Nút hủy
                    builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

                    // Hiển thị AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }
    public void loadComments() {
        progressBar2.setVisibility(View.VISIBLE);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments/" + mParam1);

        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> commentList = new ArrayList<>();

                // Duyệt qua dữ liệu comment từ Firebase
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                Log.d("CommentsLoaded", "Total comments: " + commentList.size());

                // Nếu không có comment, không gọi hàm tiếp theo
                if (commentList.isEmpty()) {
                    Log.d("Comments", "No comments found.");
                    progressBar2.setVisibility(View.GONE);
                    return;
                }else {
                    comments = commentList;
                    updateComment(comments);
                }
                progressBar2.setVisibility(View.GONE);
                
                fetchSentimentsAndSave(commentList);
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CommentsLoadError", databaseError.getMessage());
                progressBar2.setVisibility(View.GONE);
            }
        });
    }

    // Tách logic gọi server và lưu sentiment ra một hàm riêng biệt
    private void fetchSentimentsAndSave(List<Comment> commentList) {
        processNextComment(0, commentList);
    }

    private void processNextComment(int index, List<Comment> commentList) {
        if (index >= commentList.size()) {
            progressBar2.setVisibility(View.GONE);
            return;
        }

        Comment comment = commentList.get(index);

        processSentiment(sentimentResult -> {
            if (sentimentResult != null && !sentimentResult.equals("null")) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("comments").child(mParam1);

                ref.child(comment.getCommentId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            checkAndSaveSentiments(comment.getCommentId(), sentimentResult);
                            Log.d("Sentiment", (comment.getCommentId().contains("_reply")
                                    ? "Reply sentiment processed: " : "Parent sentiment processed: ") + sentimentResult);
                        } else {
                            Log.e("SentimentError", "CommentID not found under mParam1.");
                        }

                        processNextComment(index + 1, commentList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error accessing Firebase: ", databaseError.toException());
                        processNextComment(index + 1, commentList);
                    }
                });
            } else {
                Log.e("SentimentError", "Sentiment is null or invalid");
                processNextComment(index + 1, commentList);
            }
        });


    }



    private void processSentiment(OnSentimentResult callback) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("https://wttx7hth-5000.asse.devtunnels.ms/classify_comments")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        executorService.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                Log.d("SentimentDebug", "Response received with status code: " + response.code());
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("SentimentDebug", "Response body: " + responseBody);

                    try {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.fromJson(responseBody, JsonElement.class);

                        if (jsonElement.isJsonArray()) {
                            JsonArray jsonArray = jsonElement.getAsJsonArray();
                            if (jsonArray != null && jsonArray.size() > 0) {
                                Log.d("SentimentDebug", "Processing response...");
                                processResponse(jsonArray);
                            } else {
                                Log.d("SentimentDebug", "No sentiment found.");
                            }
                        } else {
                            Log.e("SentimentParsingError", "Expected a JsonArray but found something else.");
                        }

                    } catch (Exception e) {
                        Log.e("SentimentParsingError", "Parsing error occurred: ", e);
                    }

                } else {
                    Log.e("SentimentError", "Response unsuccessful with code: " + response.code());
                }
            } catch (IOException e) {
                Log.e("NetworkError", "Exception during request", e);
            }
        });
    }


    private void checkAndSaveSentiments(String commentId, String sentiment) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(mParam1); // Giới hạn Firebase query vào mParam1

        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // So sánh với các key trực tiếp từ Firebase
                        if (snapshot.getKey() != null && snapshot.getKey().equals(commentId)) {
                            // Kiểm tra nếu node là reply hoặc parent
                            if (commentId.contains("_reply")) {
                                saveSentimentToReply(snapshot, sentiment);
                            } else {
                                saveSentimentToComment(snapshot, sentiment);
                            }
                            return; // Dừng lặp sau khi tìm thấy và lưu thành công
                        }
                    }
                    Log.e("FirebaseError", "Comment ID not found under mParam1.");
                } else {
                    Log.e("FirebaseError", "No data found under mParam1.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error loading data: " + databaseError.getMessage());
            }
        });

    }

    private void saveSentimentToComment(DataSnapshot snapshot, String sentiment) {
        DatabaseReference commentRef = snapshot.getRef();
        commentRef.child("sentiment").setValue(sentiment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("SentimentSave", "Sentiment saved for parent comment: " + snapshot.getKey());
                    } else {
                        Log.e("SentimentSaveError", "Error saving sentiment: " + task.getException().getMessage());
                    }
                });
    }
    private void saveSentimentToReply(DataSnapshot snapshot, String sentiment) {
        if (snapshot.hasChild("replies")) {
            for (DataSnapshot replySnapshot : snapshot.child("replies").getChildren()) {
                String replyContent = replySnapshot.child("content").getValue(String.class);
                if (replyContent != null) {
                    replySnapshot.getRef().child("sentiment").setValue(sentiment)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.i("SentimentSave", "Sentiment saved for reply content: " + replyContent);
                                } else {
                                    Log.e("SentimentSaveError", "Error saving sentiment to reply: " + task.getException().getMessage());
                                }
                            });
                }
            }
        } else {
            Log.e("SentimentError", "No replies found for this reply node.");
        }
    }

    private void processResponse(JsonArray jsonArray) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(mParam1);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String commentIdFirebase = snapshot.getKey();
                        Log.d("FirebaseCommentID", "Checking Firebase commentID: " + commentIdFirebase);

                        // Duyệt qua từng phản hồi JSON từ server và so sánh với commentId
                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();
                            String serverCommentId = jsonObject.has("commentId") ? jsonObject.get("commentId").getAsString() : "";

                            if (serverCommentId.equals(commentIdFirebase)) {
                                String sentiment = jsonObject.has("sentiment") ? jsonObject.get("sentiment").getAsString() : "null";
                                Log.d("MatchFound", "Match found for: " + serverCommentId + " with sentiment: " + sentiment);

                                // Lưu thông tin sentiment vào Firebase
                                checkAndSaveSentiments(commentIdFirebase, sentiment);
                            }
                        }
                    }
                } else {
                    Log.e("FirebaseError", "No comments found under the given parent node.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error loading data: " + databaseError.getMessage());
            }
        });
    }








    private void openAccountFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        if (getActivity() != null) {
            Log.d("DangNhapFragment", "Opening Account Fragment");
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.e("DangNhapFragment", "Activity is null, cannot open fragment");
        }
    }

    public void getUserByEmail() {
        // Tham chiếu tới node "user"
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

        // Truy vấn tìm kiếm theo trường "email"
        Query query = userRef.orderByChild("email").equalTo(TrangThai.userEmail);

        // Lắng nghe kết quả truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lặp qua các kết quả
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String kq = userSnapshot.child("monAnDaLuu").getValue(String.class);
                        if(kq!=null){
                            if(kq.contains(ID_MONAN)){
                                imgLove.setImageResource(R.drawable.icon_loved);
                            }
                        }
                    }
                } else {
                    System.out.println("No user found with email: " + TrangThai.userEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }
    public void luuMonAn() {
        // Tham chiếu tới node "user"
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");

        // Truy vấn tìm kiếm theo trường "email"
        Query query = userRef.orderByChild("email").equalTo(TrangThai.userEmail);

        // Lắng nghe kết quả truy vấn
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lặp qua các kết quả
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String monAnDaLuu = userSnapshot.child("monAnDaLuu").getValue(String.class);

                        // Kiểm tra nếu "monAnDaLuu" không null
                        if (monAnDaLuu != null) {
                            // Chuyển đổi "monAnDaLuu" thành danh sách các ID
                            List<String> listMonAn = new ArrayList<>(Arrays.asList(monAnDaLuu.split(",")));

                            if (listMonAn.contains(ID_MONAN)) {
                                // Nếu ID_MONAN tồn tại thì xóa
                                listMonAn.remove(ID_MONAN);
                                imgLove.setImageResource(R.drawable.icon_no_love);
                                Toast.makeText(getContext(), "Đã hủy yêu thích", Toast.LENGTH_SHORT).show();
                            } else {
                                // Nếu ID_MONAN không tồn tại thì thêm
                                listMonAn.add(0,ID_MONAN);
                                imgLove.setImageResource(R.drawable.icon_loved);
                                Toast.makeText(getContext(), "Đã thích", Toast.LENGTH_SHORT).show();
                            }

                            // Cập nhật lại "monAnDaLuu" trong Firebase
                            String updatedMonAnDaLuu = String.join(",", listMonAn);
                            userSnapshot.getRef().child("monAnDaLuu").setValue(updatedMonAnDaLuu);
                        } else {
                            // Nếu "monAnDaLuu" null, khởi tạo danh sách mới
                            userSnapshot.getRef().child("monAnDaLuu").setValue(ID_MONAN);
                            imgLove.setImageResource(R.drawable.icon_loved);
                        }
                    }
                } else {
                    System.out.println("No user found with email: " + TrangThai.userEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database error: " + databaseError.getMessage());
            }
        });
    }

    public void luuLichSuXem(){
        // Lưu lại lịch sử xem nếu đã đăng nhập
        if(TrangThai.userEmail!=null && !TrangThai.userEmail.isEmpty()){
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user");
            // Truy vấn tìm kiếm theo trường "email"
            Query query = userRef.orderByChild("email").equalTo(TrangThai.userEmail);
            // Lắng nghe kết quả truy vấn
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Lặp qua các kết quả
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String monAnDaLuu = userSnapshot.child("monAnDaXem").getValue(String.class);
                            // Kiểm tra nếu "monAnDaXem" không null
                            if (monAnDaLuu != null) {
                                // Chuyển đổi "monAnDaXem" thành danh sách các ID
                                List<String> listMonAn = new ArrayList<>(Arrays.asList(monAnDaLuu.split(",")));
                                if (listMonAn.contains(String.valueOf(ID_MONAN))) {
                                    // Nếu recipeId tồn tại thì xóa
                                    listMonAn.remove(String.valueOf(ID_MONAN));
                                }else if (listMonAn.size() >= 20) {
                                    listMonAn.remove(listMonAn.size() - 1);
                                }
                                // Thêm vào đầu mảng
                                listMonAn.add(0, String.valueOf(ID_MONAN));
                                // Cập nhật lại "monAnDaLuu" trong Firebase
                                String updatedMonAnDaLuu = String.join(",", listMonAn);
                                userSnapshot.getRef().child("monAnDaXem").setValue(updatedMonAnDaLuu);
                            } else {
                                // Nếu "monAnDaLuu" null, khởi tạo danh sách mới
                                userSnapshot.getRef().child("monAnDaXem").setValue(ID_MONAN);
                            }
                        }
                    } else {
                        System.out.println("No user found with email: " + TrangThai.userEmail);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Database error: " + databaseError.getMessage());
                }
            });
        }
    }


}
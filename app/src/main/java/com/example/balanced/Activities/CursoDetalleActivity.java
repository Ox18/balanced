package com.example.balanced.Activities;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.balanced.Adapters.ListVideosCourseAdapter;
import com.example.balanced.Entity.Course;
import com.example.balanced.Entity.VideoCourseEntity;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.example.balanced.ServiceImpl.CursoServiceImpl;
import com.example.balanced.ViewModel.CourseOnlyViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CursoDetalleActivity extends ScreenCompatActivity {

    private String COURSE_ID = "";
    private CourseOnlyViewModel courseOnlyViewModel;
    private TextView txtCourseName;
    private TextView txtCourseNameProfesional;
    private TextView txtRate;
    private TextView txtCourseCountRates;
    private TextView txtCourseDescription;
    private ImageView imageCourse;
    private SharedPreferences preferences;
    private Button btnCourseAdd;
    private Button btnCourseDelete;
    private TextView txtVolver;
    private LinearLayout contentRate;
    private ListVideosCourseAdapter listVideosCourseAdapter = new ListVideosCourseAdapter();
    private RecyclerView recyclerViewVideos;
    private Button btnEditarCurso;
    private Button bntEliminarCurso;
    private Button btnAgregarLeccion;

    private Course courseLast;

    // variables to video upload
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermissions;
    private Uri videoUri;
    private Uri photoUri;
    private Boolean activePhoto = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curso_detalle);
        preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
        initCourseLoad();
        configVew();
    }

    private void initCourseLoad(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            COURSE_ID = extras.getString("id");
        }
    }

    private void configVew(){
        String uuid = preferences.getString("uuid", "____");
        listVideosCourseAdapter = new ListVideosCourseAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listVideosCourseAdapter.courseID = COURSE_ID;
        listVideosCourseAdapter.userID = uuid;
        recyclerViewVideos = (RecyclerView)findViewById(R.id.recyclerViewVideos);
        recyclerViewVideos.setAdapter(listVideosCourseAdapter);
        recyclerViewVideos.setLayoutManager(linearLayoutManager);
        recyclerViewVideos.setHasFixedSize(true);

        courseOnlyViewModel = ViewModelProviders.of(this).get(CourseOnlyViewModel.class);
        courseOnlyViewModel.courseID = COURSE_ID;
        courseOnlyViewModel.uuid = uuid;

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espera");
        progressDialog.setMessage("Cargando tu lección");
        progressDialog.setCanceledOnTouchOutside(false);

        btnEditarCurso = findViewById(R.id.btnEditarCurso);
        bntEliminarCurso = findViewById(R.id.btnCursoDelete);
        btnAgregarLeccion = findViewById(R.id.btnAgregarLeccion);
        txtVolver = findViewById(R.id.txtVolver);
        txtCourseName = findViewById(R.id.txtCourseName);
        txtCourseNameProfesional = findViewById(R.id.txtCourseNameProfesional);
        txtRate = findViewById(R.id.txtRate);
        txtCourseCountRates = findViewById(R.id.txtCourseCountRates);
        txtCourseDescription = findViewById(R.id.txtCourseDescription);
        imageCourse = findViewById(R.id.imageCourse);
        btnCourseAdd = findViewById(R.id.btnCourseAdd);
        btnCourseDelete = findViewById(R.id.btnCourseDelete);
        contentRate = findViewById(R.id.contentRate);

        loadEventClick();
        configLoad();
    }

    private void loadEventClick(){
        View.OnClickListener eventClickBack = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preferences.getString("role", "____").equals("user")){
                    LoadLobby();
                }else{
                    LoadLobbyProfesional();
                }
            }
        };

        View.OnClickListener eventClickContainerRate = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CursoDetalleActivity.this, RateActivity.class);
                intent.putExtra("id", COURSE_ID);
                startActivity(intent);
                finish();
            }
        };

        txtVolver.setOnClickListener(eventClickBack);
        contentRate.setOnClickListener(eventClickContainerRate);
    }

    private void configLoad(){
        courseOnlyViewModel.LoadById();
        courseOnlyViewModel.LoadHaveCourse();
        courseOnlyViewModel.LoadVideos();

        configObservers();
    }

    private void configObservers(){
        final Observer<Course> observerCourseOnly = new Observer<Course>() {
            @Override
            public void onChanged(Course course) {
                LoadCourseDetails(course);
            }
        };

        final Observer<Integer> observerCountRates = new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                txtCourseCountRates.setText("(" + count + " Puntuaciones)");
            }
        };

        final Observer<Boolean> observerHaveCourse = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean haveCourse) {
                if(preferences.getString("role", "____").equals("user")){
                    loadViewButtonsByUser(haveCourse);
                    listVideosCourseAdapter.active = haveCourse;
                }else{
                    LoadViewButtonsByProfessional();
                }
            }
        };

        final Observer<List<VideoCourseEntity>> observerVideoCoursesEntity = new Observer<List<VideoCourseEntity>>() {
            @Override
            public void onChanged(List<VideoCourseEntity> videoCourseEntities) {
                listVideosCourseAdapter.adicionarLista(videoCourseEntities);
            }
        };

        courseOnlyViewModel.getCourse().observe(this, observerCourseOnly);
        courseOnlyViewModel.getCountRates().observe(this, observerCountRates);
        courseOnlyViewModel.getHaveCourse().observe(this, observerHaveCourse);
        courseOnlyViewModel.getVideos().observe(this, observerVideoCoursesEntity);

        LoadEventClickBeforeStartViewModel();
    }

    private void LoadCourseDetails(Course course){
        txtCourseName.setText(course.name);
        txtCourseNameProfesional.setText(course.profesionalName);
        txtRate.setText(course.rate);
        txtCourseDescription.setText(Html.fromHtml(course.description));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.placeholder(R.drawable.ic_launcher_background);
        courseLast = course;
        Glide.with(imageCourse.getContext())
                .load(course.image)
                .centerCrop()
                .apply(requestOptions)
                .into(imageCourse);

    }

    private void loadViewButtonsByUser(Boolean haveCourse){
            if(haveCourse){
                btnCourseAdd.setVisibility(View.GONE);
                btnCourseDelete.setVisibility(View.VISIBLE);
            }else{
                btnCourseAdd.setVisibility(View.VISIBLE);
                btnCourseDelete.setVisibility(View.GONE);
            }
    }

    private View.OnClickListener btnDeleteAction(){
      return new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          try{
            CursoServiceImpl cursoService = new CursoServiceImpl();
            cursoService.DeleteById(COURSE_ID)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                  Toast.makeText(CursoDetalleActivity.this, "Se elimino el servicio", Toast.LENGTH_SHORT).show();
                  LoadLobbyProfesional();
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(CursoDetalleActivity.this, "Ha ocurrido un error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
          }catch(Exception ex){
            Toast.makeText(CursoDetalleActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
          }
        }
      };
    }

    private void LoadViewButtonsByProfessional(){
            btnEditarCurso.setVisibility(View.VISIBLE);
            btnEditarCurso.setOnClickListener(onClickEditarLeccion());
            bntEliminarCurso.setVisibility(View.VISIBLE);
            bntEliminarCurso.setOnClickListener(btnDeleteAction());
            btnAgregarLeccion.setVisibility(View.VISIBLE);
            btnAgregarLeccion.setOnClickListener(onClickAgregarLeccion());
    }

    private View.OnClickListener onClickEditarLeccion(){
      return new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          AlertDialog.Builder builder = new AlertDialog.Builder(CursoDetalleActivity.this);
          LayoutInflater inflater = getLayoutInflater();

          View vista = inflater.inflate(R.layout.dialog_personalizado, null);
          builder.setView(vista);

          AlertDialog dialog = builder.create();
          dialog.show();
          TextView tituloDialog = vista.findViewById(R.id.titulo_dialog_personalizado);
          EditText edtNombre = vista.findViewById(R.id.edtNombre);
          EditText edtDescription = vista.findViewById(R.id.edtDescription);
          Button btnCancel = vista.findViewById(R.id.btnCancel);
          Button btnConfirm = vista.findViewById(R.id.btnConfirm);
          Button btnPickPortada = vista.findViewById(R.id.btnPickPortada);

          btnConfirm.setText("Editar");
          edtNombre.setText(txtCourseName.getText());
          edtDescription.setText(txtCourseDescription.getText());

          tituloDialog.setText("Editar servicio");

          btnPickPortada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              selectImage();
            }
          });

          btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              courseLast.name = edtNombre.getText().toString();
              courseLast.description = edtDescription.getText().toString();
              guardarLeccion();
              dialog.dismiss();
            }
        });

          btnCancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            dialog.dismiss();
          }
        });

        }
      };
    }

    private void guardarLeccion(){
      progressDialog.show();

      if(activePhoto){
// timestamp
        String timestamp = ""+System.currentTimeMillis();

        // file path and name in firebase storage
        String filePathAndName = "Images/" + "image_" + timestamp;

        // storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        // upload video
        storageReference.putFile(photoUri)
          .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
              // video uploaded, get url of uploaded video
              Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
              while(!uriTask.isSuccessful());
              Uri downloadUri = uriTask.getResult();
              if(uriTask.isSuccessful()){
                courseLast.image = downloadUri.toString();
                mDatabase
                  .child("Courses")
                  .child(COURSE_ID)
                  .setValue(courseLast.getMapData())
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      Toast.makeText(getBaseContext(), "Se creo su servicio con exito", Toast.LENGTH_SHORT).show();
                      progressDialog.dismiss();
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Toast.makeText(getBaseContext(), "Falla", Toast.LENGTH_SHORT).show();
                      progressDialog.dismiss();
                    }
                  });
              }
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              progressDialog.dismiss();
              Toast.makeText(CursoDetalleActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });
      }else{
        mDatabase.child("Courses")
          .child(COURSE_ID)
          .setValue(courseLast.getMapData())
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
              Toast.makeText(CursoDetalleActivity.this, "Servicio actualizado", Toast.LENGTH_SHORT).show();
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Toast.makeText(CursoDetalleActivity.this, "No se pudo actualizar el servicio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          })
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              progressDialog.dismiss();
            }
          });
      }
    }

    private View.OnClickListener onClickAgregarLeccion(){
      return new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          AlertDialog.Builder builder = new AlertDialog.Builder(CursoDetalleActivity.this);
          LayoutInflater inflater = getLayoutInflater();

          View vista = inflater.inflate(R.layout.dialog_create_leccion, null);
          builder.setView(vista);

          AlertDialog dialog = builder.create();
          dialog.show();

          Button btnCancel = vista.findViewById(R.id.btnCancel);
          Button btnConfirm = vista.findViewById(R.id.btnConfirm);
          Button btnUploadVideo = vista.findViewById(R.id.btnUploadVideo);
          EditText edtNombre = vista.findViewById(R.id.edtNombre);
          EditText edtDescription = vista.findViewById(R.id.edtDescription);
          EditText edtNumberLection = vista.findViewById(R.id.edtNumeroLeccion);

          btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // options to display in dialog
              String[] options = { "Camera", "Gallery" };

              // dialog
              AlertDialog.Builder builderUploadVideo = new AlertDialog.Builder(CursoDetalleActivity.this);
              builderUploadVideo.setTitle("Seleccionar video desde")
                .setItems(options, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      if(i == 0){
                            // Camera
                            if(!checkCameraPermission()){
                              // camera permission not allowed
                              requestCameraPermission();
                            }else{
                              videoPickCamera();
                            }
                      }else if(i == 1){
                            // Gallery
                          videoPickGallery();
                      }
                  }
                })
                .show();
            }
          });


          btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String name = edtNombre.getText().toString();
              String description = edtDescription.getText().toString();
              String numberLection = edtNumberLection.getText().toString();
              if(TextUtils.isEmpty(name)){
                Toast.makeText(CursoDetalleActivity.this, "El nombre es requerido", Toast.LENGTH_SHORT).show();
              }
              else if(TextUtils.isEmpty(description)){
                Toast.makeText(CursoDetalleActivity.this, "La descripción es requerida", Toast.LENGTH_SHORT).show();
              }else if(videoUri == null){
                Toast.makeText(CursoDetalleActivity.this, "Es necesario un video", Toast.LENGTH_SHORT).show();
              }
              else if(TextUtils.isEmpty(numberLection)){
                Toast.makeText(CursoDetalleActivity.this, "Introduce el número de la lección", Toast.LENGTH_SHORT).show();
              }
              else{
                uploadVideoFirebase(name, description, COURSE_ID, numberLection);
                dialog.dismiss();
              }
            }
          });
          btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dialog.dismiss();
            }
          });

        }
      };
    }
    private void uploadVideoFirebase(String name, String description, String courseID, String numberLection){
      progressDialog.show();

      // timestamp
      String timestamp = ""+System.currentTimeMillis();

      // file path and name in firebase storage
      String filePathAndName = "Videos/" + "video_" + timestamp;

      // storage reference
      StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
      // upload video
      storageReference.putFile(videoUri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // video uploaded, get url of uploaded video
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while(!uriTask.isSuccessful());
            Uri downloadUri = uriTask.getResult();
            if(uriTask.isSuccessful()){
              // uri of uploaded video is received
              //String durationInString = secondsToString(mVideoView.getDuration());
              MediaPlayer mp = MediaPlayer.create(CursoDetalleActivity.this, downloadUri);
              String durationInString = secondsToString(mp.getDuration());
              // now we can add video details to our firebase db
              HashMap<String, Object> hashMap = new HashMap<>();
              hashMap.put("Title", "" + name);
              hashMap.put("description", "" + description);
              hashMap.put("number", numberLection);
              hashMap.put("time", durationInString + " min");
              hashMap.put("url", downloadUri.toString());

              mDatabase.child("Courses").child(courseID).child("Videos").child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                    Toast.makeText(CursoDetalleActivity.this, "Agregado exitosamente", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CursoDetalleActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                  }
                });

            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            progressDialog.dismiss();
            Toast.makeText(CursoDetalleActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
    }

    private void requestCameraPermission(){
      // request camera permission
      ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
      boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
      boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
      return result && result2;
    }

    private void videoPickGallery(){
      // pick video from gallery
      Intent intent = new Intent();
      intent.setType("video/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select videos"), VIDEO_PICK_GALLERY_CODE);
    }

    private void videoPickCamera(){
      // pick video from camera
      Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }

    private void LoadEventClickBeforeStartViewModel(){
        View.OnClickListener onClickAddCourse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseOnlyViewModel.BuyCourse();
                showMessageNotify("Se adquirio el curso");
            }
        };

        View.OnClickListener onClickUnsuscribeCourse = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                courseOnlyViewModel.UnsuscribeCourse();
                showMessageNotify("Se removio el curso");
            }
        };

        btnCourseAdd.setOnClickListener(onClickAddCourse);
        btnCourseDelete.setOnClickListener(onClickUnsuscribeCourse);
    }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode){
        case CAMERA_REQUEST_CODE:
          if(grantResults.length > 0){
            // check permission
            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if(cameraAccepted && storageAccepted){
              videoPickCamera();
            }else{
              Toast.makeText(this, "Camera and Storage permission are required", Toast.LENGTH_SHORT).show();
            }
          }
      }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if(resultCode == RESULT_OK){
      if(requestCode == VIDEO_PICK_GALLERY_CODE){
        videoUri = data.getData();
      }
      else if(requestCode == VIDEO_PICK_CAMERA_CODE){
         videoUri = data.getData();
      }
    }

    if(requestCode == 100 && data != null && data.getData() != null){
      photoUri = data.getData();
      activePhoto = true;
    }
      super.onActivityResult(requestCode, resultCode, data);
  }

  private String secondsToString(int pTime) {
    int seconds = pTime % 60000;
    String secondInString = Integer.toString(seconds).substring(0, 2);
    return pTime / 60000 + ":" + secondInString;
  }

  private void selectImage() {
    Intent intent = new Intent();
    intent.setType("image/");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, 100);
  }

}


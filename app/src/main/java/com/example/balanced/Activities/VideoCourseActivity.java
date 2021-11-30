package com.example.balanced.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balanced.Entity.VideoCourseEntity;
import com.example.balanced.Entity.VideoResume;
import com.example.balanced.R;
import com.example.balanced.ScreenCompatActivity;
import com.example.balanced.ServiceImpl.LeccionServiceImpl;
import com.example.balanced.ViewModel.VideoResumeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

public class VideoCourseActivity extends ScreenCompatActivity {

    private String videoURL;
    private String videoID;
    private String userID;
    private String courseID;
    private VideoResumeViewModel videoResumeViewModel;
    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;
    private ProgressBar progressBar;
    private TextView txtTitle;
    private TextView txtDescription;
    private TextView txtDuration;
    private Button btnVideoEliminar;
    private Button btnVideoEditar;
    private LinearLayout llSectionManage;
    private SharedPreferences preferences;
    private String role;
    private String numberLeccion;
    private Boolean videoIsSelected = false;
    // variables to video upload
    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermissions;
    private Uri videoUri;

    private ProgressDialog progressDialog;

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_course);
        txtDuration = findViewById(R.id.txtDuracion);
        initParams();
    }

    private void initParams(){
      preferences = getSharedPreferences("auth", Context.MODE_PRIVATE);
      role = preferences.getString("role", "nada");
      txtTitle = findViewById(R.id.txtLeccionTitle);
      txtDescription = findViewById(R.id.description);
      btnVideoEliminar = findViewById(R.id.btnVideoEliminar);
      btnVideoEditar = findViewById(R.id.btnVideoEditar);

      progressDialog = new ProgressDialog(this);
      progressDialog.setTitle("Por favor espera");
      progressDialog.setMessage("Actualizando la lección");
      progressDialog.setCanceledOnTouchOutside(false);


      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        videoID = extras.getString("videoID");
        videoURL = extras.getString("videoURL");
        courseID = extras.getString("courseID");
        userID = extras.getString("userID");
        txtTitle.setText(extras.getString("title"));
        txtDescription.setText(extras.getString("description"));
        numberLeccion = extras.getString("numberLeccion");
      }

      configView();

      btnVideoEliminar.setOnClickListener(onCLickBtnVideoEliminar());
      btnVideoEditar.setOnClickListener(onClickBtnVideoEditar());
    }

  private View.OnClickListener onClickBtnVideoEditar(){
    return new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoCourseActivity.this);
        LayoutInflater inflater = getLayoutInflater();

        View vista = inflater.inflate(R.layout.dialog_create_leccion, null);
        builder.setView(vista);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnCancel = vista.findViewById(R.id.btnCancel);
        TextView titleDialog = vista.findViewById(R.id.titleDialog);
        Button btnConfirm = vista.findViewById(R.id.btnConfirm);
        Button btnUploadVideo = vista.findViewById(R.id.btnUploadVideo);
        EditText edtNombre = vista.findViewById(R.id.edtNombre);
        EditText edtDescription = vista.findViewById(R.id.edtDescription);
        EditText edtNumberLection = vista.findViewById(R.id.edtNumeroLeccion);

        edtNumberLection.setText(numberLeccion);
        edtNombre.setText(txtTitle.getText().toString());
        edtDescription.setText(txtDescription.getText().toString());
        titleDialog.setText("Editar lección");
        btnConfirm.setText("Actualizar");

        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            // options to display in dialog
            String[] options = { "Camera", "Gallery" };

            // dialog
            AlertDialog.Builder builderUploadVideo = new AlertDialog.Builder(VideoCourseActivity.this);
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
              Toast.makeText(VideoCourseActivity.this, "El nombre es requerido", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(description)){
              Toast.makeText(VideoCourseActivity.this, "La descripción es requerida", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(numberLection)){
              Toast.makeText(VideoCourseActivity.this, "Introduce el número de la lección", Toast.LENGTH_SHORT).show();
            }
            else{
              VideoCourseEntity videoCourseEntity = new VideoCourseEntity();
              videoCourseEntity.Title = name;
              videoCourseEntity.description = description;
              videoCourseEntity.id = videoID;
              videoCourseEntity.number = numberLection;
              videoCourseEntity.url = videoURL;
              videoCourseEntity.time = "";
              uploadVideoFirebase(videoCourseEntity);
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

  private void uploadVideoFirebase(VideoCourseEntity videoCourseEntity) {
    progressDialog.show();
    if(videoIsSelected){
      // timestamp
      String timestamp = ""+System.currentTimeMillis();

      // file path and name in firebase storage
      String filePathAndName = "Videos/" + "video_" + timestamp;

      // storage reference
      StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
      storageReference.putFile(videoUri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // video uploaded, get url of uploaded video
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while(!uriTask.isSuccessful());
            Uri downloadUri = uriTask.getResult();
            if(uriTask.isSuccessful()){
              videoCourseEntity.url = downloadUri.toString();
              updateVideo(videoCourseEntity);
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(VideoCourseActivity.this, "Hubo un problema: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
          }
        });
    }else{
      updateVideo(videoCourseEntity);
    }
  }

  private void updateVideo(VideoCourseEntity videoCourseEntity){
    mDatabase.child("Courses").child(courseID).child("Videos").child(videoCourseEntity.id)
      .setValue(videoCourseEntity.getMapData())
      .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void unused) {
          updateView(videoCourseEntity);
          Toast.makeText(VideoCourseActivity.this, "¡Se actualizo la lección!", Toast.LENGTH_SHORT).show();
          progressDialog.dismiss();
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Toast.makeText(VideoCourseActivity.this, "Hubo un problema: " + e.getMessage(), Toast.LENGTH_SHORT).show();
          progressDialog.dismiss();
        }
      });
  }

  private View.OnClickListener onCLickBtnVideoEliminar(){
      return new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          LeccionServiceImpl leccionService = new LeccionServiceImpl();
          leccionService.DeleteById(videoID, courseID)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void unused) {
                Toast.makeText(VideoCourseActivity.this, "La lección se elimino satisfactoriamente.", Toast.LENGTH_SHORT).show();
                finish();
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(VideoCourseActivity.this, "No se pudo eliminar esta lección.", Toast.LENGTH_SHORT).show();
              }
            });
        }
      };
    }

    private void configView(){
      llSectionManage = findViewById(R.id.llSectionManage);

      if(role.equals("professional")){
        llSectionManage.setVisibility(View.VISIBLE);
      }


      videoResumeViewModel = ViewModelProviders.of(this).get(VideoResumeViewModel.class);
      videoResumeViewModel.course(userID, courseID, videoID);
      configObservers();
    }

    private void updateView(VideoCourseEntity videoCourseEntity) {
      txtTitle.setText(videoCourseEntity.Title);
      txtDescription.setText(videoCourseEntity.description);
      if(videoIsSelected){
        mVideoView.setVideoPath(videoCourseEntity.url);
      }
    }

    private void configObservers(){
      progressBar = (ProgressBar)findViewById(R.id.progressBar);
      mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
      mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
      mVideoView.setMediaController(mMediaController);

      mVideoView.setVideoPath(videoURL);

      mVideoView.setVideoViewCallback(new UniversalVideoView.VideoViewCallback() {
        @Override
        public void onScaleChange(boolean isFullscreen) {

        }

        @Override
        public void onPause(MediaPlayer mediaPlayer) { // Video pause
          videoResumeViewModel.save(mVideoView.getCurrentPosition(), userID, courseID, videoID);
        }

        @Override
        public void onStart(MediaPlayer mediaPlayer) { // Video start/resume to play
          videoResumeViewModel.save(mVideoView.getCurrentPosition(), userID, courseID, videoID);
        }

        @Override
        public void onBufferingStart(MediaPlayer mediaPlayer) {// steam start loading
        }

        @Override
        public void onBufferingEnd(MediaPlayer mediaPlayer) {// steam end loading

        }

      });

      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          progressBar.setVisibility(View.GONE);
          mVideoView.setVisibility(View.VISIBLE);
          mMediaController.setVisibility(View.VISIBLE);
          String durationInString = secondsToString(mVideoView.getDuration());
          txtDuration.setText(durationInString + " min");
        }
      });

      final Observer<VideoResume> observerCourseOnly = new Observer<VideoResume>() {
        @Override
        public void onChanged(VideoResume videoResume) {
          mVideoView.seekTo(videoResume.minute);
        }
      };

      videoResumeViewModel.getVideoResume().observe(this, observerCourseOnly);
    }

    @Override
    public void finish(){
      videoResumeViewModel.save(mVideoView.getCurrentPosition(), userID, courseID, videoID);
      super.finish();
    }

    private String secondsToString(int pTime) {
      int seconds = pTime % 60000;
      String secondInString = Integer.toString(seconds).substring(0, 2);
      return pTime / 60000 + ":" + secondInString;
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
        videoIsSelected = true;
      }
      else if(requestCode == VIDEO_PICK_CAMERA_CODE){
        videoUri = data.getData();
        videoIsSelected = true;
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }
}

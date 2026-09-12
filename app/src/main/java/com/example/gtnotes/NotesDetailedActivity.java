package com.example.gtnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NotesDetailedActivity extends AppCompatActivity {
    EditText note_title_edittext,note_content_edittext;
    ImageButton save_note_btn ;

    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    ImageButton deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_detailed);

        note_title_edittext = findViewById(R.id.notes_title_edittext);
        note_content_edittext = findViewById(R.id.notes_content_edittext);
        save_note_btn = findViewById(R.id.notes_save_note_btn);

        pageTitleTextView = findViewById(R.id.add_new_note_tv);
        deleteNoteTextViewBtn  = findViewById(R.id.delete_btn);

        //receive data
        title = getIntent().getStringExtra("title");
        content= getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        note_title_edittext.setText(title);
        note_content_edittext.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        save_note_btn.setOnClickListener(view -> savenotes());

        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase() );
    }

    void savenotes() {
        String note_title = note_title_edittext.getText().toString();
        String note_content = note_content_edittext.getText().toString();

        if(note_title.isEmpty()|| note_title==null){
            note_title_edittext.setError("Title is Required..");
            return;
        }

        if(note_content.isEmpty()||note_content==null){
            note_content_edittext.setError("content is Empty..");
            return;
        }
        Note note = new Note();
        note.setTitle(note_title);
        note.setContent(note_content);
        note.setTimestamp(Timestamp.now());

        Save_Note_To_Firebase(note);
    }
    void Save_Note_To_Firebase(Note note){
        DocumentReference documentReference ;
        documentReference = Utility.Get_Collection_Reference_For_Notes().document();

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // note added successfully
                    Toast.makeText(NotesDetailedActivity.this, "Note Saved ", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    // note failed to save
                    Toast.makeText(NotesDetailedActivity.this, "Failed To Save", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.Get_Collection_Reference_For_Notes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Toast.makeText(NotesDetailedActivity.this, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(NotesDetailedActivity.this, "Failed While Deleting notes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
package group9.tcss450.uw.edu.chatappgroup9;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void sendMessage(View view){
        Intent intent = new Intent(this, NavigationActivity.class);
        EditText Name = (EditText) findViewById(R.id.Name);
        EditText Pass = (EditText) findViewById(R.id.Password);
        String Name1 = Name.getText().toString();
        String Password = Pass.getText().toString();
        if (Name1 != null && Name1.contains("@") && Password != null){
            startActivity(intent);
        }
    }
}

package webclever.sliding_menu;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Validator.Validator;

/**
 * Created by Женя on 17.08.2015.
 */
public class ActivityChangePassword extends FragmentActivity {
    private Button buttonSaveChanges;
    private EditText editTextOldPass;
    private EditText editTextNewPass;
    private EditText editTextCNewPass;
    private Validator validator;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        validator = new Validator();

        editTextOldPass = (EditText) findViewById(R.id.editTextOldPassword);
        editTextNewPass = (EditText) findViewById(R.id.editTextNewPassword);
        editTextCNewPass = (EditText) findViewById(R.id.editTextConfirmNewPassword);
        buttonSaveChanges = (Button) findViewById(R.id.buttonSaveChanges);
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringNewPass = editTextNewPass.getText().toString();
                String stringCNewPass = editTextCNewPass.getText().toString();
                if (stringNewPass.length() > 0 && stringCNewPass.equals(stringNewPass)){
                    if (validator.isPasswordValid(stringNewPass)){
                        Toast.makeText(getApplicationContext(),"Зміни збережено!",Toast.LENGTH_SHORT).show();
                        editTextNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
                        editTextCNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
                        ActivityChangePassword.this.finish();

                    }else
                    {
                        editTextNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                        editTextCNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                    }
                }else {
                    editTextNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                    editTextCNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

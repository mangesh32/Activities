package mg.activities;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsersDAO {
   static FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
     static DatabaseReference mUsersDB= firebaseDatabase.getReference("users");

    public static void addUser(String loginid,String name,String email,String password){
        User user=new User(name,email,password);
        mUsersDB.child(loginid).setValue(user);
    }


}

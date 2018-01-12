package tshirt.rajeev.com.tshirt.Helper;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by dell on 11/12/2017.
 */

public class utility {
    public static boolean validateName(String name)
    {
        if(name.equalsIgnoreCase("") || name.length()<=3)
            return false;
        else return  true;
    }
    public static boolean validateMobile(String mob)
    {
        if(mob.equalsIgnoreCase("") || mob.length()!=10)
            return false;
        else
            return true;
    }
    public static void alertD(Context context, String mess)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Alert");
        alert.setMessage(mess);
        alert.setPositiveButton("OK",null);
        alert.show();
    }
}

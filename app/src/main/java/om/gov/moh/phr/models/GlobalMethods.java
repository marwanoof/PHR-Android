package om.gov.moh.phr.models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.example.awesomedialog.AwesomeDialog;

import om.gov.moh.phr.R;

import static om.gov.moh.phr.models.MyConstants.LANGUAGE_ARABIC;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_PREFS;
import static om.gov.moh.phr.models.MyConstants.LANGUAGE_SELECTED;

public class GlobalMethods {
    public static String getStoredLanguage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        return sharedPref.getString(LANGUAGE_SELECTED, LANGUAGE_ARABIC);
    }
    public static void storeLanguage(String language,Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(LANGUAGE_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANGUAGE_SELECTED, language);
        editor.apply();
    }
    public static void setAppLanguage(String language) {
        AppLanguage appLanguage = AppLanguage.getInstance();
        appLanguage.setSelectedLanguage(language);
    }
    public static Bitmap flipImage(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right);
// create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);

        Bitmap flipped_bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return flipped_bitmap;

    }
    public static String setPatientClassArabicName(String patientClass){
        String result;
        switch (patientClass) {
            case "outpatient":
                result = "العيادات الخارجية";
                break;
            case "emergency":
                result = "الطوارئ";
                break;
            case "inpatient":
                result = "العيادات الداخلية";
                break;
            default:
                result = patientClass;
                break;
        }
        return result;
    }

    public static String convertToArabicNumber(String strNum){
        //int number = Integer.parseInt(strNum);

        String[] numberArr = strNum.split("");
        String finalResult = "";
        for (int i = 0; i<numberArr.length;i++){
            if (numberArr[i].equals("0"))
                finalResult += "٠";
            else if (numberArr[i].equals("1"))
                finalResult += "١";
            else if (numberArr[i].equals("2"))
                finalResult += "٢";
            else if (numberArr[i].equals("3"))
                finalResult += "٣";
            else if (numberArr[i].equals("4"))
                finalResult += "٤";
            else if (numberArr[i].equals("5"))
                finalResult += "٥";
            else if (numberArr[i].equals("6"))
                finalResult += "٦";
            else if (numberArr[i].equals("7"))
                finalResult += "٧";
            else if (numberArr[i].equals("8"))
                finalResult += "٨";
            else if (numberArr[i].equals("9"))
                finalResult += "٩";


        }
        return finalResult;
    }
    public static String convertToEnglishNumber(String strNum){
        //int number = Integer.parseInt(strNum);

        String[] numberArr = strNum.split("");
        String finalResult = "";
        for (int i = 0; i<numberArr.length;i++){
            if (numberArr[i].equals("٠"))
                finalResult += "0";
            else if (numberArr[i].equals("١"))
                finalResult += "1";
            else if (numberArr[i].equals("٢"))
                finalResult += "2";
            else if (numberArr[i].equals("٣"))
                finalResult += "3";
            else if (numberArr[i].equals("٤"))
                finalResult += "4";
            else if (numberArr[i].equals("٥"))
                finalResult += "5";
            else if (numberArr[i].equals("٦"))
                finalResult += "6";
            else if (numberArr[i].equals("٧"))
                finalResult += "7";
            else if (numberArr[i].equals("٨"))
                finalResult += "8";
            else if (numberArr[i].equals("٩"))
                finalResult += "9";
            else if (numberArr[i].equals("."))
                finalResult += ".";
            /*else
                finalResult = strNum;*/


        }
        return finalResult;
    }

    public static void displayDialog(String msg ,Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton(
                context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
    public static void showAlertDialog(Context context){



    }
}

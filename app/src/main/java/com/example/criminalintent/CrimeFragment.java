package com.example.criminalintent;

import static com.example.criminalintent.R.id.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
public class CrimeFragment extends Fragment{
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mCallButton;
    private Callbacks mCallbacks;
    public static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeFragment_Crime_ID";
    public static final String SELECTION_DIALOG_TAG = "Selection";
    public static final String DATE_DIALOG_TAG = "Date";
    private static final String DIALOG_IMAGE = "image";
    public static final String EXTRA_DATE = "com.example.criminalintent.CrimeFragment_Crime_Date";
    public static final String TAG = "CrimeFragment";
    public static final int REQUEST_DATE = 0;
    private static final int REQUEST_CHOOSE_DATE = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_CONTACT = 3;
    private static final int REQUEST_DIAL = 4;
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        //Log.d("getParentAN", " "+NavUtils.getParentActivityName(getActivity()));
        if(NavUtils.getParentActivityName(getActivity())!=null)
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleField = (EditText)v.findViewById(crime_title);
        mTitleField.setText(mCrime.getmTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(
                    CharSequence c, int start, int before, int count) {
                mCrime.setmTitle(c.toString());
                mCallbacks.onCrimeUpdated(mCrime);
            }
            public void beforeTextChanged(
                    CharSequence c, int start, int count, int after) {
                // This space intentionally left blank
            }
            public void afterTextChanged(Editable c) {
                // This one too
            }
        });
        mDateButton = (Button)v.findViewById(crime_date);
        mDateButton.setText(mCrime.getmDate().toString());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                //set request code and show selection Dialog
                DateTimeSelectionFragment dialog = new DateTimeSelectionFragment();//CrimeFragment.newInstance(mCrime.getmDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_CHOOSE_DATE);
                dialog.show(fm, SELECTION_DIALOG_TAG);
            }
        });
        mSolvedCheckBox = (CheckBox)v.findViewById(crime_solved);
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mCrime.setmSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
            }
        });
        mPhotoButton = (ImageButton)v.findViewById(crime_imageButton);
        // if camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        //Log.d("CrimeFragment", "hasSystemFeature(feature): "+!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA));
        //Log.d("CrimeFragment", "hasSystemFeature(feature): "+!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT));
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mPhotoButton.setEnabled(false);
        }
        //Log.d(TAG, "mPhotoButton.isEnabled():"+mPhotoButton.isEnabled());
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // launch the camera activity
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getmPhoto();
                if (p == null)
                    return;

                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                String path = getActivity()
                        .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path)
                        .show(fm, DIALOG_IMAGE);
            }
        });
        registerForContextMenu(mPhotoView);
        Button reportButton = (Button)v.findViewById(crime_reportButton);
        reportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });
        mSuspectButton = (Button)v.findViewById(crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });
        mCallButton = (Button)v.findViewById(make_callButton);
        mCallButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mCrime.getmNumber()!=null){
                    Intent i = new Intent(Intent.ACTION_DIAL, mCrime.getmNumber());
                    startActivity(i);
                }
            }
        });
        /*PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(, 0);
        boolean isIntentSafe = activities.size() > 0;*/
        return v;
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }
    @Override
    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.d("delete_item", String.valueOf(item.getItemId()));
        if(item.getItemId()==android.R.id.home){
            if(NavUtils.getParentActivityName(getActivity())!=null)
                NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }else if(item.getItemId()== menu_item_delete_crime){
            CrimeLab.get(getActivity()).deleteCrime(mCrime);
            //adapter.notifyDataSetChanged();
            Intent i = new Intent(getActivity(), CrimeListActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart(){
        super.onStart();
        showPhoto();
    }
    @Override
    public void onStop(){
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }
    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeID);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        if(resultCode!= -1) return;
        Log.d("moveToFirst", " "+requestCode);
        if(requestCode == REQUEST_CHOOSE_DATE){
            if((boolean)i.getSerializableExtra("hasChooseDate") == true){
                DatePickerFragment dpf = DatePickerFragment.newInstance(mCrime.getmDate());
                dpf.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dpf.show(getActivity().getSupportFragmentManager(), DATE_DIALOG_TAG);
            }else{
                TimePickerFragment tpf = TimePickerFragment.newInstance(mCrime.getmDate());
                tpf.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                tpf.show(getActivity().getSupportFragmentManager(), DATE_DIALOG_TAG);
            }
        }
        else if(requestCode==REQUEST_DATE){
            Date date = (Date) i.getSerializableExtra(EXTRA_DATE);
            mCrime.setmDate(date);
            mDateButton.setText(mCrime.getmDate().toString());
            mCallbacks.onCrimeUpdated(mCrime);
        }
        else if(requestCode == REQUEST_PHOTO){
            Photo oldPhoto = mCrime.getmPhoto();
            if (oldPhoto != null) {
                String Oldpath = getActivity()
                        .getFileStreamPath(oldPhoto.getFilename()).getAbsolutePath();
                File imageFile = new File(Oldpath);
                boolean deleted = imageFile.delete();
                Log.d("delete_check", "deleted: "+ deleted);
            }
            String filename = (String) i.getSerializableExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                String path = getActivity()
                        .getFileStreamPath(filename).getAbsolutePath();
                Photo p = new Photo(filename);
                mCrime.setmPhoto(p);
                showPhoto();
            }
            mCallbacks.onCrimeUpdated(mCrime);
            Log.d(TAG, "filename: "+filename);
        }
        else if (requestCode==REQUEST_CONTACT){
            Uri contactData = i.getData();
            Cursor c =  getActivity().managedQuery(contactData, null, null, null, null);
            Log.d("moveToFirst", "moveToFirst:"+c.moveToFirst());
            if (c.moveToFirst()) {
                mCrime.setmSuspect(c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
                mCallButton.setText(mCrime.getmSuspect());
                String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String hasPhone =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                Log.d("moveToFirst", "hasPhone:"+hasPhone.equalsIgnoreCase("1"));
                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getActivity().getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                            null, null);
                    phones.moveToFirst();
                    mCrime.setmNumber(Uri.parse("tel:" +
                            phones.getString(phones.getColumnIndexOrThrow("data1"))));
                    Log.d("moveToFirst", "tel:"+phones.getString(phones.getColumnIndexOrThrow("data1")));
                }
            }
            mCallbacks.onCrimeUpdated(mCrime);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v == mPhotoView)
            getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.d("delete_id", " "+item.getItemId());
        if(item.getItemId()== menu_item_delete_crime){
            Photo removedPhoto = mCrime.getmPhoto();
            if(removedPhoto!=null){
                Photo oldPhoto = mCrime.getmPhoto();
                String Oldpath = getActivity()
                        .getFileStreamPath(oldPhoto.getFilename()).getAbsolutePath();
                File imageFile = new File(Oldpath);
                boolean deleted = imageFile.delete();
                Log.d("delete_check", "deleted: "+ deleted);
                mPhotoView.setImageDrawable(null);
                mCrime.setmPhoto(null);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_list_item_context, menu);
    }
    private void showPhoto() {
        // (Re)set the image button's image based on our photo
        Photo p = mCrime.getmPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }
        mPhotoView.setImageDrawable(b);
    }
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.ismSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateString = new SimpleDateFormat("EEE, MMM dd, YYYY").format(mCrime.getmDate());
        String suspect = mCrime.getmSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getmTitle(), dateString, solvedString, suspect);
        return report;
    }
}

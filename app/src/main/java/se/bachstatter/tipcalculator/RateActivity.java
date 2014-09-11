package se.bachstatter.tipcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;



public class RateActivity extends Activity implements OnItemSelectedListener,
        OnChronometerTickListener, OnSeekBarChangeListener, View.OnFocusChangeListener {

    //State consonants
    public final static int OK = 0;
    public final static int BAD = -1;
    public final static int GOOD = 1;

    //EditText variables
    EditText editTextBill;
    TextView textViewTip;
    EditText editTextTotal;

    //Textview
    TextView textViewTipPercent;

    //Radiobutton variables
    int radioGroupState;

    //Spinner variables
    Spinner psSpinner;
    int spinnerState;

    //Switch variables
    Switch switchGenerous;

    //Seekbar variable
    SeekBar seekBarChangeTip;

    //Chronometer variables
    Chronometer chrono;
    Boolean chronoIsCounting = false;
    long timeWhenStopped = 0;

    // State variables, standard tip percent is 10%.
    Integer tipPercent = 10;

    /**
     * On create run initilizespinner, run setVariablesAndListeners
     * and set
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        initializeSpinner();
        setVariablesAndListeners();
    }

    /**
     * Sets all variables and listenters
     * Also sets seekbar to tipPercent 10%.
     */
    private void setVariablesAndListeners() {
        editTextBill = (EditText)findViewById(R.id.editTextBill);
        editTextBill.setOnFocusChangeListener(this);
        textViewTip = (TextView)findViewById(R.id.textViewTip);
        editTextTotal = (EditText)findViewById(R.id.editTextTotal);

        textViewTipPercent = (TextView)findViewById(R.id.textViewTipPercent);

        switchGenerous = (Switch)findViewById(R.id.switchGenerous);

        seekBarChangeTip = (SeekBar)findViewById(R.id.seekBarChangeTip);
        seekBarChangeTip.setProgress(tipPercent);
        seekBarChangeTip.setOnSeekBarChangeListener(this);

        chrono = (Chronometer)findViewById(R.id.chrono);
        chrono.setOnChronometerTickListener(this);
    }


    /**
     * Initialize the spinner
     * Find the spinner item with findViewById()
     * Create an ArrayAdapter using the string array and a default spinner layout
     * Specify the layout to use when the list of choices appears
     * And finally add the adapter to the spinner
     */
    public void initializeSpinner(){
        psSpinner = (Spinner) findViewById(R.id.psSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ps_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        psSpinner.setAdapter(adapter);
        psSpinner.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    /**
     * See description in the method
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.chronoStart:
                //Start from 0 + time when stopped
                // Set is counting to true to stop double click on pause button.
                // Stat the chronometer
                chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronoIsCounting = true;
                chrono.start();
                break;
            case R.id.chronoPaus:
                // If chronometer is counting set the elapsed time to timewhen stopped
                // Stop the chonometer
                // Set is counting to false to stop double click on pause button.
                if (chronoIsCounting){
                    timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                    chrono.stop();
                    chronoIsCounting = false;
                }
                break;
            case R.id.chronoReset:
                //reset the timewhenstop variable
                // set chrono is counting to false
                //stop the chronometer
                // and reset the time
                timeWhenStopped = 0;
                chronoIsCounting = false;
                chrono.stop();
                chrono.setBase(SystemClock.elapsedRealtime());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On checkbox click check which checkbox got clicked.
     * If it already was checked subtract 1 from tipPercent
     * If it was unchecked add 1 to tipPercent
     *
     * Finnaly TODO
     * @param view
     */
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBoxFriendly:
                if (checked)
                    tipPercent += 1;
                else
                    tipPercent -= 1;
                break;
            case R.id.checkBoxSpecials:
                if (checked)
                    tipPercent += 1;
                else
                    tipPercent -= 1;
                break;
            case R.id.checkBoxOpinion:
                if (checked)
                    tipPercent += 1;
                else
                    tipPercent -= 1;
                break;
        }
        setTextToTextViews();
    }

    /**
     * On radio button click check which button was clicked
     * and run checkStateReturnTipPercentChange() and
     * send it both chosen value and the state (last chosen value)
     * After that set the radioGroupState to current value
     * Finally TODO
     *
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioGood:
                if (checked) {
                    tipPercent += checkStateReturnTipPercentChange(GOOD, radioGroupState);
                    radioGroupState = GOOD;
                }
                    break;
            case R.id.radioOk:
                if (checked) {
                    tipPercent += checkStateReturnTipPercentChange(OK, radioGroupState);
                    radioGroupState = OK;
                }
                break;
            case R.id.radioBad:
                if (checked) {
                    tipPercent += checkStateReturnTipPercentChange(BAD, radioGroupState);
                    radioGroupState = BAD;
                }
                break;
        }
        setTextToTextViews();
    }

    /**
     * Checks the state (the last value the user picked) if its the firsttime or ok we return the chosen value
     * If the state is good we need to subtract 1 from the chosen value.
     * If the state is bad we need to add 1 to the chosen value.
     * If we dont do this the the tip amount will go crazy if the user
     * for example clicks: "good" "ok" "good" "ok" "good" "ok" "good" the tip amount would have added 4 percent
     * instead of 1 which is the right amount.
     *
     *
     * @param chosenValue The value the user has chosen right now
     * @param state The value the user choosed last time, if first time = 0
     * @return int  that represents the correct change given the state.
     */
    private int checkStateReturnTipPercentChange(int chosenValue, int state) {
        switch (state){
            case OK:
                return chosenValue;
            case GOOD:
                return chosenValue-1;
            case BAD:
                return chosenValue+1;
        }
        return 0;

    }

    /**
     * On item selected run checkStateReturnTipPercentChange() and
     * send it both chosen value and the state (last chosen value)
     * After that set the spinnerState to current value
     * And finally convert TODO
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                tipPercent += checkStateReturnTipPercentChange(OK, spinnerState);
                spinnerState = OK;
                break;
            case 1:
                tipPercent += checkStateReturnTipPercentChange(BAD, spinnerState);
                spinnerState = BAD;
                break;
            case 2:
                tipPercent += checkStateReturnTipPercentChange(GOOD, spinnerState);
                spinnerState = GOOD;
        }
        setTextToTextViews();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * On switch click check is the switch is on.
     * If it is on add 5 to tipPercent
     * If it is off subtract 5 from tipPercent
     * Finally TODO
     * @param view
     */
    public void onSwitchClick(View view) {
        boolean on = ((Switch) view).isChecked();
        if(on){
            tipPercent += 5;
        }else{
            tipPercent -= 5;
        }
        setTextToTextViews();


    }

    /**
     * On chronometer tick get elapsed seconds. elapsedRealTime-chronometer.getBase will give milliseconds
     * Thats why I divide it by 1000.
     * If elapsedSeconds % 30 == 0 means 30 seconds has gone.
     * I also check so that elapsed seconds isnt 0 since all values under 1000 miliseconds will be
     * rounded down to 0.
     * Then I subtract 1 from the tipPercent.
     *
     * In english: subtract 1 from tipPercent every 30 seconds
     * And finally TODO
     *
     * @param chronometer
     */
    @Override
    public void onChronometerTick(Chronometer chronometer) {
        int elapsedSeconds = (int) (SystemClock.elapsedRealtime() - chronometer.getBase())/1000;

        if ( ( elapsedSeconds % 30 ) == 0 && elapsedSeconds != 0 ){
            tipPercent -= 1;
            setTextToTextViews();
        }
    }

    /**
     * One slider to rule them all.
     * takes the slider progress/value and sets it to tipPercent variable
     * Finally TODO
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tipPercent = progress;
        setTextToTextViews();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void setTextToTextViews(){
        textViewTipPercent.setText(String.valueOf(tipPercent));
        seekBarChangeTip.setProgress(tipPercent);
        if (editTextBill.getText().length() != 0){
            Double bill =Double.parseDouble(editTextBill.getText().toString());
            Double percent = Double.parseDouble(String.valueOf(tipPercent))/100;

            Double tip = bill * percent;
            tip = (double)Math.round(tip * 100) / 100;

            Double total = bill + tip;
            total = (double)Math.round(total * 100) / 100;

            editTextTotal.setText(String.valueOf(total));
            textViewTip.setText(String.valueOf(tip));
        }else{
            editTextTotal.setText("");
            textViewTip.setText("");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
            setTextToTextViews();
    }
}

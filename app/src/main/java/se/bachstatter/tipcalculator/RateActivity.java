package se.bachstatter.tipcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View.OnFocusChangeListener;
import android.widget.Toast;


public class RateActivity extends Activity implements OnItemSelectedListener,
        OnChronometerTickListener, OnSeekBarChangeListener, OnFocusChangeListener {

    //State consonants
    private final static int OK = 0;
    private final static int BAD = -1;
    private final static int GOOD = 1;
    private final static String GREEDY_TOAST = "That's mean, please give a few percent";

    //EditText variables
    private EditText editTextBill;

    //Textview
    private TextView textViewTip;
    private TextView textViewTotal;
    private TextView textViewTipPercent;

    //Radiobutton variable
    private int radioGroupState;

    //Spinner variable
    private int spinnerState;

    //Seekbar variable
    private SeekBar seekBarChangeTip;

    //Chronometer variables
    private Chronometer mChronometer;
    private Boolean chronoIsCounting = false;
    private long timeWhenStopped = 0;

    // State variables, standard tip percent is 10%.
    private Integer tipPercent = 10;

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
        textViewTotal = (TextView)findViewById(R.id.textViewTotal);

        textViewTipPercent = (TextView)findViewById(R.id.textViewTipPercent);

        seekBarChangeTip = (SeekBar)findViewById(R.id.seekBarChangeTip);
        seekBarChangeTip.setProgress(tipPercent);
        seekBarChangeTip.setOnSeekBarChangeListener(this);

        mChronometer = (Chronometer)findViewById(R.id.chrono);
        mChronometer.setOnChronometerTickListener(this);
    }


    /**
     * Initialize the spinner
     * Find the spinner item with findViewById()
     * Create an ArrayAdapter using the string array and a default spinner layout
     * Specify the layout to use when the list of choices appears
     * And finally add the adapter to the spinner
     */
    void initializeSpinner(){
        Spinner psSpinner = (Spinner) findViewById(R.id.psSpinner);
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
                // Stat the mChronometer
                mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronoIsCounting = true;
                mChronometer.start();
                break;
            case R.id.chronoPaus:
                // If mChronometer is counting set the elapsed time to timewhen stopped
                // Stop the chonometer
                // Set is counting to false to stop double click on pause button.
                if (chronoIsCounting){
                    timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
                    mChronometer.stop();
                    chronoIsCounting = false;
                }
                break;
            case R.id.chronoReset:
                //reset the timewhenstop variable
                // set mChronometer is counting to false
                //stop the mChronometer
                // and reset the time
                timeWhenStopped = 0;
                chronoIsCounting = false;
                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On checkbox click check which checkbox got clicked.
     * If it already was checked subtract 1 from tipPercent
     * If it was unchecked add 1 to tipPercent
     *
     * Finnaly run setTextToTextViews()
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
     * Finally setTextToTextViews()
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
     * Checks the state (the last value the user picked) if its the first time or ok we return the chosen value
     * If the state is good we need to subtract 1 from the chosen value.
     * If the state is bad we need to add 1 to the chosen value.
     * If we dont do this the the tip amount will go crazy if the user
     * for example clicks: "good" "ok" "good" "ok" "good" "ok" "good" the tip amount would have added 4 percent
     * instead of 1 which is the right amount.
     *
     *
     * @param chosenValue The value the user has chosen right now
     * @param state The value the user chose last time, if first time = 0
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
     * And finally setTextToTextViews()
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
     * Finally run setTextToTextViews()
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
     * On mChronometer tick get elapsed seconds. elapsedRealTime-mChronometer.getBase will give milliseconds
     * Thats why I divide it by 1000.
     * If elapsedSeconds % 30 == 0 means 30 seconds has gone.
     * I also check so that elapsed seconds isnt 0 since all values under 1000 miliseconds will be
     * rounded down to 0.
     * Then I subtract 1 from the tipPercent.
     * And finally run setTextToTextViews()
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
     * Finally run setTextToTextViews()
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tipPercent = progress;
        if (tipPercent == 0){
            Toast.makeText(this, GREEDY_TOAST, Toast.LENGTH_LONG).show();
        }
        setTextToTextViews();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


    /**
     * TODO comment new if
     * Sets tip percent text view and the seekbar to current tipPercent.
     * if Bill text not is empty get the bill value and parse it to double
     * Parse the tipPercent variable to decimals
     * Calculate and round the tip to two decimals.
     * Calculate and round the total to two decimals
     * Set these values to total textview and tip textview
     *
     * else if bill edittext was empty, empty percent and total textview
     *
     */
    private void setTextToTextViews(){
        if(tipPercent >= 0) {
            textViewTipPercent.setText(String.valueOf(tipPercent));
            seekBarChangeTip.setProgress(tipPercent);

            if (editTextBill.getText().length() != 0) {
                Double bill = Double.parseDouble(editTextBill.getText().toString());
                Double percent = Double.parseDouble(String.valueOf(tipPercent)) / 100;

                Double tip = bill * percent;
                tip = (double) Math.round(tip * 100) / 100;

                Double total = bill + tip;
                total = (double) Math.round(total * 100) / 100;

                textViewTotal.setText(String.valueOf(total));
                textViewTip.setText(String.valueOf(tip));
            } else {
                textViewTotal.setText("");
                textViewTip.setText("");
            }
        }
    }

    /**
     * If it doesnt have focus and has triggered a focus change (blur) run setTextToTextViews()
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
            setTextToTextViews();
    }
}

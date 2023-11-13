package ca.lifesaver.engineers.it.vital.tracker;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphHistory extends Fragment implements OnChartValueSelectedListener {
    // Declare all three charts
    private LineChart heartRateChart;
    private LineChart oxygenLevelChart;
    private LineChart bodyTempChart;

    // Declare data sets for each chart
    private LineDataSet heartRateDataSet;
    private LineDataSet oxygenLevelDataSet;
    private LineDataSet bodyTempDataSet;

    private LineChart lineChart;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private ArrayList<Entry> values;

    public GraphHistory() {
        // Required empty public constructor
    }

    public static GraphHistory newInstance(String param1, String param2) {
        return new GraphHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Setup each chart
        heartRateChart = view.findViewById(R.id.HeartRateChart);
        oxygenLevelChart = view.findViewById(R.id.OxygenLevelChart);
        bodyTempChart = view.findViewById(R.id.BodyTempChart);

        setupGraph(heartRateChart, "Heart Rate History");
        setupGraph(oxygenLevelChart, "Oxygen Level History");
        setupGraph(bodyTempChart, "Body Temperature History");

        // Fetch and display data for each chart
        fetchDataFromFirestore();
        fetchDataFromFirestore();
    }
    private void fetchDataFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Replace with actual user ID

            db.collection("userId").document(userId)
                    .collection("vitals").document("data")
                    .get().addOnCompleteListener(task -> {if (task.isSuccessful() && task.getResult() != null) {
                        // Assuming 'vitalsData' is an array of maps
                        List<Map<String, Object>> vitalsDataList = (List<Map<String, Object>>) task.getResult().get("vitalsData");
                        if (vitalsDataList != null) {
                            for (Map<String, Object> vitalsData : vitalsDataList) {
                                // Extract the data
                                Number heartRate = (Number) vitalsData.get("heartRate");
                                Number oxygenLevel = (Number) vitalsData.get("oxygenLevel");
                                Number bodyTemp = (Number) vitalsData.get("bodyTemp");

                                // Update your charts here
                                if (heartRate != null) {
                                    updateGraph(heartRateChart, heartRate.intValue());
                                }
                                if (oxygenLevel != null) {
                                    updateGraph(oxygenLevelChart, oxygenLevel.intValue());
                                }
                                if (bodyTemp != null) {
                                    updateGraph(bodyTempChart, bodyTemp.intValue());
                                }
                            }
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                    });
        }
    }
    private void setupGraph(LineChart chart, String label) {
        values = new ArrayList<>();
        chart.setOnChartValueSelectedListener(this);
        lineDataSet = createSet();
        lineData = new LineData(lineDataSet);
        chart.setData(lineData);

        configureGraphAppearance(chart);
    }

    private void configureGraphAppearance(LineChart chart) {
        Legend l = chart.getLegend();
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.rgb(104, 241, 175));
        chart.getDescription().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setMaxHighlightDistance(300);
        chart.setDrawGridBackground(false);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);
        chart.animateXY(2000, 2000);
    }

    private void updateGraph(LineChart chart, int value) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), value), 0);
            data.notifyDataChanged();

            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(10);
            chart.moveViewToX(data.getEntryCount());
        }
    }

    private void updateGraphAppearance() {
        // Your existing code for updating the graph's appearance
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.WHITE);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.WHITE);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setCubicIntensity(0.2f);
        set.setDrawFilled(true);
        set.setDrawCircles(false);
        set.setDrawHorizontalHighlightIndicator(false);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // Handle value selection
    }

    @Override
    public void onNothingSelected() {
        // Handle no selection
    }
}
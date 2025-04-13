package com.adamya.unitconverter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ViewHolder> {

    private final List<Conversion> conversions;

    public ConversionAdapter(List<Conversion> conversions) {
        this.conversions = conversions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversion conversion = conversions.get(position);
        holder.valueFrom.setText(String.format("%.2f %s", conversion.valueFrom, conversion.unitFrom));
        holder.valueTo.setText(String.format("%.2f %s", conversion.valueTo, conversion.unitTo));
        holder.arrowText.setText(" = ");
    }

    @Override
    public int getItemCount() {
        return conversions.size();
    }

    public void updateConversions(List<Conversion> newConversions) {
        this.conversions.clear();
        this.conversions.addAll(newConversions);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView valueFrom;
        public TextView arrowText;
        public TextView valueTo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valueFrom = itemView.findViewById(R.id.valueFrom);
            arrowText = itemView.findViewById(R.id.arrowText);
            valueTo = itemView.findViewById(R.id.valueTo);
        }
    }

    // Model class for a conversion
    public static class Conversion {
        public double valueFrom;
        public String unitFrom;
        public double valueTo;
        public String unitTo;

        public Conversion(double valueFrom, String unitFrom, double valueTo, String unitTo) {
            this.valueFrom = valueFrom;
            this.unitFrom = unitFrom;
            this.valueTo = valueTo;
            this.unitTo = unitTo;
        }
    }
} 
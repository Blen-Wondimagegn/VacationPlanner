package com.example.vacationapplication.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationapplication.R;
import com.example.vacationapplication.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;

    public ExcursionAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            String title = current.getTitle();
            int vacID = current.getVacationId();
            holder.excursionItemView.setText(title); // Updated to display the title
            holder.excursionItemView2.setText(Integer.toString(vacID));
        } else {
            holder.excursionItemView.setText("No excursion title");
            holder.excursionItemView2.setText("No vacation id");
        }
    }

    public void setExcursions(List<Excursion> excursions) {
        this.mExcursions = excursions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mExcursions != null) {
            return mExcursions.size();
        } else {
            return 0;
        }
    }

    class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionItemView;
        private final TextView excursionItemView2;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.textView2);
            excursionItemView2 = itemView.findViewById(R.id.textView3);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final Excursion current = mExcursions.get(position);
                        Intent intent = new Intent(context, ExcursionDetails.class);
                        intent.putExtra("id", current.getId());
                        intent.putExtra("title", current.getTitle());
                        intent.putExtra("date", current.getDate());
                        intent.putExtra("vacationId", current.getVacationId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}

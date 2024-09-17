package com.example.vacationapplication.UI;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vacationapplication.R;
import com.example.vacationapplication.entities.Vacation;
import java.util.List;


public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {


    class VacationViewHolder extends RecyclerView.ViewHolder{
        private final TextView vacationItemView;
        private VacationViewHolder(View itemView){
            super(itemView);
            vacationItemView=itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    final Vacation current=mVacations.get(position);
                    Intent intent=new Intent(context,VacationDetails.class);
                    intent.putExtra("id", current.getId());
                    intent.putExtra("title", current.getTitle());
                    intent.putExtra("hotel", current.getHotel());
                    intent.putExtra("startDate", current.getStartDate());
                    intent.putExtra("endDate", current.getEndDate());
                    context.startActivity(intent);
                }
            });
        }
    }
    private List<Vacation> mVacations;
    private final Context context;
    private final LayoutInflater mInflater;

    public VacationAdapter(Context context){
        mInflater=LayoutInflater.from(context);
        this.context=context;
    }
    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=mInflater.inflate(R.layout.vacation_list_item,parent,false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if(mVacations!=null){
            Vacation current=mVacations.get(position);
            String title=current.getTitle();
            holder.vacationItemView.setText(title);
        }
        else{
            holder.vacationItemView.setText("No Vacation");
        }
    }

    public void setVacations(List<Vacation> vacations){
        if (vacations != null) {
            mVacations = vacations;
            notifyDataSetChanged();
        } else {
            Log.e("VacationAdapter", "Vacations list is null"); // Log error
        }
    }

    @Override
    public int getItemCount() {
        return mVacations.size();
    }
}

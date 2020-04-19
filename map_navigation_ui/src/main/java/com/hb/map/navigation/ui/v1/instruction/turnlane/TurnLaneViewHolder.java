package com.hb.map.navigation.ui.v1.instruction.turnlane;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.hb.map.navigation.ui.v1.R;

class TurnLaneViewHolder extends RecyclerView.ViewHolder {

    TurnLaneView turnLaneView;

    TurnLaneViewHolder(View itemView) {
        super(itemView);
        turnLaneView = itemView.findViewById(R.id.turnLaneView);
    }
}

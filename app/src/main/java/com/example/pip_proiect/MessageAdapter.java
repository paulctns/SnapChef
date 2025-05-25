package com.example.pip_proiect;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter pentru {@link RecyclerView} care afișează o listă de mesaje.
 * Fiecare element din listă este reprezentat printr-un obiect {@link Message}.
 * <p>
 * Clasa adaptează datele din lista de mesaje în view-uri corespunzătoare pentru afișare.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;

    /**
     * Constructor pentru {@code MessageAdapter}.
     *
     * @param messages Lista de mesaje ce urmează a fi afișate în RecyclerView.
     */
    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * ViewHolder pentru elementul de listă reprezentând un mesaj.
     * Păstrează referința la componenta TextView unde se afișează textul mesajului.
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;

        /**
         * Constructor pentru {@code MessageViewHolder}.
         *
         * @param view View-ul corespunzător elementului din listă.
         */
        public MessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.messageText);
        }
    }

    /**
     * Creează un nou ViewHolder pentru un element din listă.
     * Este apelat când RecyclerView are nevoie să creeze o nouă instanță de ViewHolder.
     *
     * @param parent   ViewGroup-ul părinte în care va fi plasat noul ViewHolder.
     * @param viewType Tipul view-ului (poate fi util pentru listă cu tipuri diferite de itemi).
     * @return Un nou obiect {@code MessageViewHolder}.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    /**
     * Leagă datele unui mesaj din lista {@code messages} la un ViewHolder.
     * Este apelat când RecyclerView afișează un element.
     *
     * @param holder   ViewHolder-ul în care vor fi puse datele mesajului.
     * @param position Poziția elementului în listă.
     */
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.messageText.setText(messages.get(position).getText());
    }

    /**
     * Returnează numărul total de elemente din lista de mesaje.
     *
     * @return Dimensiunea listei {@code messages}.
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }
}

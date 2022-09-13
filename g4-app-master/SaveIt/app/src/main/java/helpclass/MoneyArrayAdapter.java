package helpclass;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saveit.R;

import java.util.List;

/**
 * The Money array adapter.
 * @author Zilin.Song
 */
public class MoneyArrayAdapter extends ArrayAdapter<Money> {

    private final Integer resourceId;


    /**
     * Instantiates a new Money array adapter.
     *
     * @param context       the context
     * @param resourceId    the resource id
     * @param moneyListList the money list list
     */
    public MoneyArrayAdapter(final Context context, final Integer resourceId, final List<Money> moneyListList) {
        super(context, resourceId, moneyListList);
        this.resourceId = resourceId;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        final View listItem = (convertView != null) ? convertView :
                LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        final Money money = getItem(position);
        final TextView value = listItem.findViewById(R.id.value);
        final TextView date = listItem.findViewById(R.id.date);
        final TextView instructions = listItem.findViewById(R.id.instruction);

        value.setText(money.getValue());
        date.setText(money.getDate());
        instructions.setText(money.getInstructions());
        if (money.getType().equals("Cost")){// cost will be Green
            value.setTextColor(Color.GREEN);
        }
        else
            value.setTextColor(Color.RED);//income will be Red.
        value.setTypeface(value.getTypeface(), Typeface.BOLD);

        return listItem;
    }
}
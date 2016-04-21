package net.avenwu.overscrolllayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.avenwu.overscrolllayout.dummy.DummyContent;

public class ListActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getSupportFragmentManager().beginTransaction().add(R.id.content, ItemFragment.newInstance(1)).commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}

package net.lzzy.cinemanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.fragments.AddCinemasFragment;
import net.lzzy.cinemanager.fragments.AddOrdersFragment;
import net.lzzy.cinemanager.fragments.BaseFragment;
import net.lzzy.cinemanager.fragments.CinemasFragment;
import net.lzzy.cinemanager.fragments.OnFragmentIntInteractionListener;
import net.lzzy.cinemanager.fragments.OrdersFragment;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.utils.ViewUtils;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
,AddCinemasFragment.OnFragmentIntInteractionListener, AddCinemasFragment.OnCinemaCreatedListener,
        CinemasFragment.OnCinemaSelectedListener, OnFragmentIntInteractionListener
{
   private FragmentManager manager = getSupportFragmentManager();

    private View layoutMenu;
    private SearchView search;
    private TextView tvTitle;
    private LinearLayout layoutAddOrder;
    private SparseArray<String> titleArray
            = new SparseArray<>();
    public static final String EXTRA_CINEMA_ID = "cinemaId";
    private SparseArray <Fragment> fragmentArray =new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setTitleMenu();
        search.setOnQueryTextListener(new ViewUtils.AbstractQueryHandler() {
            @Override
            public boolean handleQuery(String kw) {
                Fragment fragment =manager.findFragmentById(R.id.forever_container);
                if (fragment!= null){

                    if (fragment instanceof BaseFragment){
                        ((BaseFragment)fragment).search(kw);
                    }
                }
                return  true;
            }
        });
    }
    private void setTitleMenu() {
        titleArray.put(R.id.bar_title_tv_add_cinema,"添加影院");
        titleArray.put(R.id.bar_title_tv_view_cinema,"影院列表");
        titleArray.put(R.id.bar_title_tv_add_order,"添加订单");
        titleArray.put(R.id.bar_title_tv_view_order,"我的订单");
        layoutMenu = findViewById(R.id.bar_title_layout_menu);
        layoutMenu.setVisibility(View.GONE);
        findViewById(R.id.bar_title_img_menu).setOnClickListener(v -> {
            int visible = layoutMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            layoutMenu.setVisibility(visible);
                }
        );
        tvTitle = findViewById(R.id.bar_title_tv_title);
        tvTitle.setText(R.string.bar_title_menu_orders);
        search =findViewById(R.id.main_sv_search);
        findViewById(R.id.bar_title_tv_add_cinema).setOnClickListener( this);
        findViewById(R.id.bar_title_tv_view_cinema).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_add_order).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_view_order).setOnClickListener(this);
        findViewById(R.id.bar_title_tv_exit).setOnClickListener(v -> System.exit(0));

    }
    @Override
    public void onClick(View v) {
        layoutMenu.setVisibility(View.GONE);
        tvTitle.setText(titleArray.get(v.getId()));
        FragmentTransaction transaction =manager.beginTransaction();
        Fragment fragment = fragmentArray.get(v.getId());
        if (fragment == null){

            fragment = createFragment(v.getId());
            fragmentArray.put(v.getId(),fragment);
            transaction.add(R.id.forever_container,fragment);
        }
        for (Fragment f:manager.getFragments()){
            transaction.hide(f);

        }
        transaction.show(fragment).commit();

        }


    private Fragment createFragment( int id) {
        switch (id){
            case  R.id.bar_title_tv_add_cinema:
                return new AddCinemasFragment();
            case R.id.bar_title_tv_view_cinema:
                return new CinemasFragment();
            case R.id.bar_title_tv_add_order:
                return new AddOrdersFragment();
            case R.id.bar_title_tv_view_order:
                return new OrdersFragment();
                default:
                    return  new OrdersFragment();
        }

    }

        @Override
        public void hideSearch(){
        search.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelAddOrder() {

    }

    private void hideAndShow(int hideId,int showId){
        Fragment hideFragment = fragmentArray.get(showId);
        if (hideFragment == null){
            return;
        }
        Fragment showFragment = fragmentArray.get(showId);
        FragmentTransaction transaction = manager.beginTransaction();
        if (showFragment == null){
            showFragment = createFragment(showId);
            fragmentArray.put(showId,showFragment);
            transaction.add(R.id.forever_container,showFragment);

        }
        transaction.hide(hideFragment).show(showFragment).commit();
        tvTitle.setText(titleArray.get(showId));
    }
    @Override
    public void cabcelAddCinema() {
        Fragment addCinemaFragment = fragmentArray
                .get(R.id.bar_title_tv_add_cinema);
        if (addCinemaFragment ==null){
            return;
        }
        Fragment cinemasFragment = fragmentArray.get(R.id.bar_title_tv_view_cinema);

        FragmentTransaction transaction =manager.beginTransaction();
        if (cinemasFragment == null){
            cinemasFragment = new CinemasFragment();
            fragmentArray.put(R.id.bar_title_tv_view_cinema,cinemasFragment);
            transaction.add(R.id.forever_container,cinemasFragment);
        }
      transaction.hide(addCinemaFragment).show(cinemasFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_title_tv_view_cinema));
    }
    @Override
    public void saveCinema(Cinema cinema) {
        Fragment addCinemaFragment = fragmentArray
                .get(R.id.bar_title_tv_add_cinema);
        if (addCinemaFragment ==null){
            return;}
        Fragment cinemasFragment = fragmentArray.get(R.id.bar_title_tv_view_cinema);
        FragmentTransaction transaction = manager.beginTransaction();
        if (cinemasFragment == null){
            cinemasFragment = new CinemasFragment(cinema);
            //创建cinemaFragment同时传cinema进来
            fragmentArray.put(R.id.bar_title_tv_view_cinema,cinemasFragment);
            transaction.add(R.id.forever_container,cinemasFragment);

        }else {
            ((CinemasFragment) cinemasFragment).save(cinema);
            transaction.hide(addCinemaFragment).show(cinemasFragment).commit();
            tvTitle.setText(titleArray.get(R.id.bar_title_tv_view_cinema));

        }
        transaction.hide(addCinemaFragment).show(cinemasFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_title_tv_view_cinema));
    }
    public void saveOrder(Order order){
        Fragment addOroderFragment = fragmentArray.get(R.id.bar_title_tv_add_order);
        if (addOroderFragment == null){
            return;
        }
        Fragment ordersFragment = fragmentArray.get(R.id.bar_title_tv_add_order);
        FragmentTransaction transaction = manager.beginTransaction();
        if (ordersFragment == null) {
            //创建CinemasFragment同时传Cinemas进来
            ordersFragment = new OrdersFragment(order);
            fragmentArray.put(R.id.bar_title_tv_view_order, ordersFragment);
            transaction.add(R.id.forever_container, ordersFragment);
        }else {
            ((OrdersFragment)ordersFragment).save(order);
        }
        transaction.hide(addOroderFragment).show(ordersFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_title_tv_view_order));
    }

    @Override
    public void onCinemaSelected(String cinemaId) {

        Intent intent = new Intent(this, CinemaOrdersActivity.class);
        intent.putExtra(MainActivity.EXTRA_CINEMA_ID,cinemaId);
        startActivity(intent);

    }
}


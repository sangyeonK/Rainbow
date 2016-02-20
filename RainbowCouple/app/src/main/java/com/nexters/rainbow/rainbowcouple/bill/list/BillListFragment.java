package com.nexters.rainbow.rainbowcouple.bill.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nexters.rainbow.rainbowcouple.R;
import com.nexters.rainbow.rainbowcouple.bill.Bill;
import com.nexters.rainbow.rainbowcouple.bill.BillApi;
import com.nexters.rainbow.rainbowcouple.bill.add.BillAddDialog;
import com.nexters.rainbow.rainbowcouple.common.BaseFragment;
import com.nexters.rainbow.rainbowcouple.common.network.ExceptionHandler;
import com.nexters.rainbow.rainbowcouple.common.network.NetworkManager;
import com.nexters.rainbow.rainbowcouple.common.widget.EndlessListView;
import com.nexters.rainbow.rainbowcouple.graph.GraphActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class BillListFragment extends BaseFragment implements BillAddDialog.AddDialogDismissCallback {

    private static final String TAG_BILL_LIST_FRAGMENT = "bill_list_fragment";

    private View rootView;
    private List<Bill> billList = new ArrayList<>();
    private BillListAdapter billListAdapter;

    // TODO: 2016. 1. 16. listView loadNewData는 디비에 저장된 데이터 읽어오는 것으로
    @Bind(R.id.listViewBill) EndlessListView billListView;
    @Bind(R.id.textViewBillEmpty) TextView emptyTextView;
    @Bind(R.id.actionBtnAddBill) Button actionBtnAddBill;

    public static BillListFragment newInstance() {
        BillListFragment fragment = new BillListFragment();
        fragment.setFragmentTag(TAG_BILL_LIST_FRAGMENT);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bill_list, container, false);
        ButterKnife.bind(this, rootView);

        setFragmentTag(TAG_BILL_LIST_FRAGMENT);

        billListAdapter = new BillListAdapter(getActivity(), R.layout.list_item_bill, billList);
        billListView.setAdapter(billListAdapter);
        billListView.setEmptyView(emptyTextView);

        BillApi billApi = NetworkManager.getApi(BillApi.class);
        Observable<List<Bill>> billObservable = billApi.viewBill(
                "3ynZKDkeVEloO79JnocmI0OUUjyzRWIuKZcLpYCtFID5p1Pdys-1-RDhFShhiBn_", "1", "2016", "2", "9"
        );

        bind(billObservable)
                .subscribe(new Action1<List<Bill>>() {
                    @Override
                    public void call(List<Bill> bills) {
                        billListAdapter.addAllData(bills);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        new ExceptionHandler(getActivity()).handle(throwable);
                    }
                });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // TODO: 2016. 1. 16. option Menu에 동기화 버튼을 둬서 수동 동기화 하는 것은 어떨까 ?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.actionBtnAddBill)
    public void showBillAddDialog() {
        BillAddDialog billAddDialog = BillAddDialog.newInstance();
        billAddDialog.setDismissCallback(this);
        billAddDialog.show(getFragmentManager(), billAddDialog.getFragmentTag());
    }

    // TODO: 2016. 1. 16. api로 서버에 저장 할 것. DB에 따로 저장하지는 않음..
    // TODO: 2016. 1. 16. listView 자체 저장 하고 DB는 fragment 다시 불러올 때 서버와 동기화 한 후 가져올 것
    @Override
    public void saveNewBill(Bill bill) {
        billListAdapter.addData(0, bill);
        billListView.setSelection(0);

        Snackbar.make(actionBtnAddBill, "새로운 지출 내역이 저장되었습니다.", Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btnGraph)
    void openGraph() {
        Intent graphActivity = new Intent(getActivity(), GraphActivity.class);
        startActivity(graphActivity);
    }
}
package com.manimarank.spell4wiki.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.manimarank.spell4wiki.R;
import com.manimarank.spell4wiki.adapters.ContributorsAdapter;
import com.manimarank.spell4wiki.adapters.CoreContributorsAdapter;
import com.manimarank.spell4wiki.apis.ApiClient;
import com.manimarank.spell4wiki.apis.ApiInterface;
import com.manimarank.spell4wiki.models.ContributorData;
import com.manimarank.spell4wiki.models.Contributors;
import com.manimarank.spell4wiki.models.CoreContributors;
import com.manimarank.spell4wiki.utils.GeneralUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContributorsActivity extends AppCompatActivity {

    private List<Contributors> contributorsList = new ArrayList<>();
    private List<CoreContributors> coreContributorsList = new ArrayList<>();
    private RecyclerView recyclerViewCodeContributors, recyclerViewCoreContributors;
    private AppCompatTextView txtHelpers;
    private View loadingContributors, layoutCoreContributors, tabCore, tabCode;
    private ContributorsAdapter contributorsAdapter;
    private CoreContributorsAdapter coreContributorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.contributors));
        }

        recyclerViewCoreContributors = findViewById(R.id.recyclerViewCoreContributors);
        recyclerViewCodeContributors = findViewById(R.id.recyclerViewCodeContributors);
        txtHelpers = findViewById(R.id.txtHelpers);
        loadingContributors = findViewById(R.id.loadingContributors);
        layoutCoreContributors = findViewById(R.id.layoutCoreContributors);

        contributorsAdapter = new ContributorsAdapter(this, contributorsList);
        recyclerViewCodeContributors.setAdapter(contributorsAdapter);
        recyclerViewCodeContributors.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        coreContributorsAdapter = new CoreContributorsAdapter(this, coreContributorsList);
        recyclerViewCoreContributors.setAdapter(coreContributorsAdapter);
        recyclerViewCoreContributors.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        loadCoreContributorsAndHelpersFromApi();
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1)
                    loadCodeContributorsFromApi();
                else
                    loadCoreContributorsAndHelpersFromApi();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadCoreContributorsAndHelpersFromApi() {

        loadingContributors.setVisibility(View.VISIBLE);
        layoutCoreContributors.setVisibility(View.GONE);
        recyclerViewCodeContributors.setVisibility(View.GONE);

        if (GeneralUtils.isNetworkConnected(getApplicationContext())) {

            ApiInterface api = ApiClient.getApi().create(ApiInterface.class);
            Call<ContributorData> call = api.fetchContributorData();

            call.enqueue(new Callback<ContributorData>() {
                @Override
                public void onResponse(@NotNull Call<ContributorData> call, @NotNull Response<ContributorData> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        coreContributorsList.clear();
                        coreContributorsList.addAll(response.body().getCore_contributors());
                        if (coreContributorsAdapter != null)
                            coreContributorsAdapter.notifyDataSetChanged();

                        StringBuilder wikiTechHelpers = new StringBuilder();
                        response.body().getWiki_tech_helpers();
                        if (response.body().getWiki_tech_helpers().size() > 0) {
                            for (String helper : response.body().getWiki_tech_helpers()) {
                                wikiTechHelpers.append(helper).append("\n");
                            }
                            txtHelpers.setText(wikiTechHelpers.toString());
                        }

                        if (loadingContributors != null)
                            loadingContributors.setVisibility(View.GONE);
                        if (layoutCoreContributors != null)
                            layoutCoreContributors.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onFailure(@NotNull Call<ContributorData> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            GeneralUtils.showSnack(recyclerViewCodeContributors, getString(R.string.check_internet));
        }
    }

    private void loadCodeContributorsFromApi() {
        loadingContributors.setVisibility(View.VISIBLE);
        recyclerViewCodeContributors.setVisibility(View.GONE);
        layoutCoreContributors.setVisibility(View.GONE);

        ApiInterface api = ApiClient.getApi().create(ApiInterface.class);
        Call<List<Contributors>> call = api.fetchCodeContributorsList();

        call.enqueue(new Callback<List<Contributors>>() {
            @Override
            public void onResponse(@NotNull Call<List<Contributors>> call, @NotNull Response<List<Contributors>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contributorsList.clear();
                    contributorsList.addAll(response.body());
                    if (recyclerViewCodeContributors != null && recyclerViewCodeContributors.getAdapter() != null)
                        recyclerViewCodeContributors.getAdapter().notifyDataSetChanged();
                }

                if (loadingContributors != null)
                    loadingContributors.setVisibility(View.GONE);
                if (recyclerViewCodeContributors != null)
                    recyclerViewCodeContributors.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NotNull Call<List<Contributors>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }


}

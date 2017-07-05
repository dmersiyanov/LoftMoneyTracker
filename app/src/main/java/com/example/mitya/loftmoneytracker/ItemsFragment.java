package com.example.mitya.loftmoneytracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mitya.loftmoneytracker.api.AddResult;
import com.example.mitya.loftmoneytracker.api.LSApi;
import com.example.mitya.loftmoneytracker.api.Result;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.mitya.loftmoneytracker.AddActivity.RC_ADD_ITEM;

/**
 * Created by Mitya on 26.06.2017.
 */

public class ItemsFragment extends Fragment {
    public static final String ARG_TYPE = "type";

    private static final int LOADER_ITEMS = 0;
    private static final int LOADER_ADD = 1;
    private static final int LOADER_REMOVE = 2;

    private String type;
    private LSApi api;
    private ItemsAdapter adapter = new ItemsAdapter();
    private View add;
    private ActionMode actionMode;
    SwipeRefreshLayout refresh;
    private ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            actionMode.getMenuInflater().inflate(R.menu.items, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    // удаление с сервера еще не реализовано, но вставил для след. занятия
                    //for (Integer selectedItemId : adapter.getSelectedItems())
                    // removeItem();
                    new AlertDialog.Builder(getContext())
                            .setCancelable(false)
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.confirm_remove)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    for (int i = adapter.getSelectedItems().size() - 1; i >= 0; i--)
                                        removeItem(adapter.remove(adapter.getSelectedItems().get(i)));
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    actionMode.finish();
                                }
                            })
                            .show();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            add.setVisibility(View.VISIBLE);
            actionMode = null;
            adapter.clearSelections();


        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.items, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView items = (RecyclerView) view.findViewById(R.id.items);
        items.setAdapter(adapter);

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (actionMode == null) {
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallBack);
                    add.setVisibility(View.INVISIBLE);
                }
                toggleSelection(e, items);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggleSelection(e, items);
                return super.onSingleTapConfirmed(e);
            }


        });

        items.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems();
            }
        });
        add = view.findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(AddActivity.EXTRA_TYPE, type);
                startActivityForResult(intent, RC_ADD_ITEM);

            }
        });

        type = getArguments().getString(ARG_TYPE);
        api = ((LsApp) getActivity().getApplication()).api();

        loadItems();
    }

    private void toggleSelection(MotionEvent e, RecyclerView items) {
        adapter.toggleSelection(items.getChildLayoutPosition(items.findChildViewUnder(e.getX(), e.getY())));
        String title = adapter.getSelectedItemCount() + " выбрано";
        actionMode.setTitle(title);
    }

    private void loadItems() {
        getLoaderManager().initLoader(LOADER_ITEMS, null, new LoaderManager.LoaderCallbacks<List<Item>>() {

            @Override
            public Loader<List<Item>> onCreateLoader(int id, Bundle args) {

                return new AsyncTaskLoader<List<Item>>(getContext()) {
                    @Override
                    public List<Item> loadInBackground() {
                        try {
                            return api.items(type).execute().body();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
                if (data == null) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.clear();
                    adapter.addAll(data);
                    refresh.setRefreshing(false);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Item>> loader) {
            }
        }).forceLoad();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_ADD_ITEM && resultCode == RESULT_OK) {
            Item item = (Item) data.getSerializableExtra(AddActivity.RESULT_ITEM);
            Toast toast = Toast.makeText(getContext(), item.name + " " + item.price, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void addItem(final Item item) {
        getLoaderManager().restartLoader(LOADER_ADD, null, new LoaderManager.LoaderCallbacks<AddResult>() {

            @Override
            public Loader<AddResult> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<AddResult>(getContext()) {
                    @Override
                    public AddResult loadInBackground() {
                        try {
                            return api.add(item.name, item.price, item.type).execute().body();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<AddResult> loader, AddResult data) {
                adapter.updateId(item, data.id);

            }

            @Override
            public void onLoaderReset(Loader<AddResult> loader) {

            }
        }).forceLoad();
    }

    private void removeItem(final Item item) {
        getLoaderManager().restartLoader(LOADER_REMOVE, null, new LoaderManager.LoaderCallbacks<Result>() {

            @Override
            public Loader<Result> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Result>(getContext()) {
                    @Override
                    public Result loadInBackground() {
                        try {
                            return api.remove(item.id).execute().body();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Result> loader, Result data) {

            }

            @Override
            public void onLoaderReset(Loader<Result> loader) {

            }
        }).forceLoad();
    }
}
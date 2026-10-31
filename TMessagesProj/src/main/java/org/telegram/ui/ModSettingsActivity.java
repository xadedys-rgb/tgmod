/*
 * This is the source code of Telegram for Android v. 7.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2020.
 */

package org.telegram.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.ModConfig;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ModSettingsActivity extends BaseFragment {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_EDIT = 2;
    private static final int TYPE_INFO = 3;

    private static final int ROW_GENERAL_HEADER = 0;
    private static final int ROW_MOD_ENABLED = 1;
    private static final int ROW_FAKE_STARS = 2;
    private static final int ROW_FAKE_STARS_INFO = 3;
    private static final int ROW_GIFT_ANIM_HEADER = 4;
    private static final int ROW_FAKE_GIFT_ANIM = 5;
    private static final int ROW_FAKE_GIFT_ANIM_INFO = 6;
    private static final int ROW_COUNT = 7;

    private RecyclerListView listView;
    private ListAdapter adapter;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.ModSettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView = frameLayout;

        listView = new RecyclerListView(context);
        listView.setSections();
        actionBar.setAdaptiveBackground(listView);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new ListAdapter(context));
        listView.setOnItemClickListener((view, position) -> onRowClicked(position));
        frameLayout.addView(listView, LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT
        ));
        return fragmentView;
    }

    private void onRowClicked(int position) {
        if (position == ROW_MOD_ENABLED) {
            boolean newValue = !ModConfig.isModEnabled(getParentActivity());
            ModConfig.setModEnabled(getParentActivity(), newValue);
            adapter.notifyItemChanged(ROW_FAKE_STARS);
            adapter.notifyItemChanged(ROW_FAKE_GIFT_ANIM);
            return;
        }
        if (position == ROW_FAKE_GIFT_ANIM) {
            boolean newValue = !ModConfig.isFakeGiftAnimEnabled(getParentActivity());
            ModConfig.setFakeGiftAnimEnabled(getParentActivity(), newValue);
            adapter.notifyItemChanged(ROW_FAKE_GIFT_ANIM);
            return;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;
        private EditTextSettingsCell fakeStarsCell;

        private ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return ROW_COUNT;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            boolean modEnabled = ModConfig.isModEnabled(getParentActivity());
            if (position == ROW_MOD_ENABLED) {
                return true;
            }
            if (!modEnabled) {
                return false;
            }
            return position == ROW_FAKE_STARS || position == ROW_FAKE_GIFT_ANIM;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_HEADER) {
                view = new HeaderCell(context);
            } else if (viewType == TYPE_CHECK) {
                view = new TextCheckCell(context);
            } else if (viewType == TYPE_EDIT) {
                view = new EditTextSettingsCell(context);
            } else {
                view = new TextInfoPrivacyCell(context);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            ));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean modEnabled = ModConfig.isModEnabled(getParentActivity());
            if (holder.getItemViewType() == TYPE_HEADER) {
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (position == ROW_GENERAL_HEADER) {
                    cell.setText(LocaleController.getString(R.string.ModSettingsGeneral));
                } else if (position == ROW_GIFT_ANIM_HEADER) {
                    cell.setText(LocaleController.getString(R.string.ModSettingsGiftAnimation));
                }
            } else if (holder.getItemViewType() == TYPE_CHECK) {
                TextCheckCell cell = (TextCheckCell) holder.itemView;
                cell.setEnabled(position == ROW_MOD_ENABLED || modEnabled);
                if (position == ROW_MOD_ENABLED) {
                    cell.setTextAndCheck(
                            LocaleController.getString(R.string.ModSettingsEnableVisualMod),
                            modEnabled,
                            false
                    );
                } else if (position == ROW_FAKE_GIFT_ANIM) {
                    cell.setTextAndCheck(
                            LocaleController.getString(R.string.ModSettingsGiftAnimation),
                            ModConfig.isFakeGiftAnimEnabled(getParentActivity()),
                            false
                    );
                }
            } else if (holder.getItemViewType() == TYPE_EDIT) {
                EditTextSettingsCell cell = (EditTextSettingsCell) holder.itemView;
                fakeStarsCell = cell;
                cell.setEnabled(modEnabled);
                cell.setTextAndHint(
                        String.valueOf(ModConfig.getFakeStars(getParentActivity())),
                        LocaleController.getString(R.string.ModSettingsFakeStarsHint),
                        true
                );
                cell.getTextView().removeTextChangedListener(textWatcher);
                cell.getTextView().addTextChangedListener(textWatcher);
            } else {
                TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                if (position == ROW_FAKE_STARS_INFO) {
                    cell.setText(LocaleController.getString(R.string.ModSettingsFakeStarsInfo));
                } else if (position == ROW_FAKE_GIFT_ANIM_INFO) {
                    cell.setText(LocaleController.getString(R.string.ModSettingsGiftAnimationInfo));
                }
            }
        }

        private final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.isEmpty()) {
                    try {
                        long value = Long.parseLong(text);
                        ModConfig.setFakeStars(getParentActivity(), value);
                    } catch (NumberFormatException ignored) {}
                }
            }
        };

        @Override
        public int getItemViewType(int position) {
            if (position == ROW_GENERAL_HEADER || position == ROW_GIFT_ANIM_HEADER) {
                return TYPE_HEADER;
            }
            if (position == ROW_MOD_ENABLED || position == ROW_FAKE_GIFT_ANIM) {
                return TYPE_CHECK;
            }
            if (position == ROW_FAKE_STARS_INFO || position == ROW_FAKE_GIFT_ANIM_INFO) {
                return TYPE_INFO;
            }
            if (position == ROW_FAKE_STARS) {
                return TYPE_EDIT;
            }
            return TYPE_INFO;
        }
    }
}
package com.lxy.test.whv.ui.contact;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.model.Room;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.avoscloud.leanchatlib.utils.Constants;
import com.lxy.test.whv.App;
import com.lxy.test.whv.R;
import com.lxy.test.whv.entity.SortUser;
import com.lxy.test.whv.service.AddRequestManager;
import com.lxy.test.whv.service.CacheService;
import com.lxy.test.whv.service.ConversationManager;
import com.lxy.test.whv.service.event.ContactRefreshEvent;
import com.lxy.test.whv.service.event.InvitationEvent;
import com.lxy.test.whv.ui.base_activity.BaseFragment;
import com.lxy.test.whv.ui.chat.ChatRoomActivity;
import com.lxy.test.whv.ui.view.BaseListView;
import com.lxy.test.whv.ui.view.EnLetterView;
import com.lxy.test.whv.util.CharacterParser;
import com.lxy.test.whv.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by LXY on 2015/10/16.
 * 联系人 & 朋友请求 & chat  不再保留单独的联系人选项卡，建议使用搜索功能来查找自己的朋友
 */
public class ContactFragment extends BaseFragment {

    private static CharacterParser characterParser;
    private static PinyinComparator pinyinComparator;
    @InjectView(R.id.dialog)
    TextView dialogTextView;
    @InjectView(R.id.list_friends)
    BaseListView<SortUser> friendsList;
    @InjectView(R.id.right_letter)
    EnLetterView rightLetter;
    View listHeaderView;
    private ContactFragmentAdapter userAdapter;
    private ListHeaderViewHolder listHeaderViewHolder = new ListHeaderViewHolder();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        ButterKnife.inject(this, view);

        listHeaderView = inflater.inflate(R.layout.contact_fragment_header_layout, null, false);
        ButterKnife.inject(listHeaderViewHolder, listHeaderView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        initHeader();
        initListView();
        initRightLetterViewAndSearchEdit();
        refresh();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNewRequestBadge();
    }

    private void initRightLetterViewAndSearchEdit() {
        rightLetter.setTextView(dialogTextView);
        rightLetter.setOnTouchingLetterChangedListener(new LetterListViewListener());
    }

    private void initHeader() {
        headerLayout.showTitle(App.ctx.getString(R.string.contact));
        //TODO 右侧不要加朋友按钮，放点什么呢...
//        headerLayout.showRightImageButton(R.drawable.base_action_bar_add_bg_selector, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ctx, ContactAddFriendActivity.class);
//                ctx.startActivity(intent);
//            }
//        });
    }

    private List<SortUser> convertAVUser(List<AVUser> datas) {
        List<SortUser> sortUsers = new ArrayList<SortUser>();
        int total = datas.size();
        for (int i = 0; i < total; i++) {
            AVUser avUser = datas.get(i);
            SortUser sortUser = new SortUser();
            sortUser.setInnerUser((LeanchatUser) avUser);
            String username = avUser.getUsername();
            if (!TextUtils.isEmpty(username)) {
                String pinyin = characterParser.getSelling(username);
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]")) {
                    sortUser.setSortLetters(sortString.toUpperCase());
                } else {
                    sortUser.setSortLetters("#");
                }
            } else {
                sortUser.setSortLetters("#");
            }
            sortUsers.add(sortUser);
        }
        Collections.sort(sortUsers, pinyinComparator);
        return sortUsers;
    }

    private void initListView() {
        userAdapter = new ContactFragmentAdapter(getActivity());
        friendsList.init(new BaseListView.DataFactory<SortUser>() {
            @Override
            public List<SortUser> getDatasInBackground(int skip, int limit, List<SortUser> currentDatas) throws Exception {
                return convertAVUser(findFriends());
            }
        }, userAdapter);

        friendsList.addHeaderView(listHeaderView, null, false);
        friendsList.setPullLoadEnable(false);
        friendsList.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getActivity().getWindow().getAttributes().softInputMode !=
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    View currentFocus = getActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
        friendsList.setItemListener(new BaseListView.ItemListener<SortUser>() {
            @Override
            public void onItemSelected(SortUser item) {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra(Constants.MEMBER_ID, item.getInnerUser().getObjectId());
                startActivity(intent);
            }

            @Override
            public void onItemLongPressed(SortUser item) {
                showDeleteDialog(item);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        // TODO Auto-generated method stub
        if (isVisibleToUser) {
            //refreshMsgsFromDB();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void updateNewRequestBadge() {
        listHeaderViewHolder.getMsgTipsView().setVisibility(
                AddRequestManager.getInstance().hasUnreadRequests() ? View.VISIBLE : View.GONE);
    }

    private void refresh() {
        friendsList.onRefresh();
        AddRequestManager.getInstance().countUnreadRequests(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                updateNewRequestBadge();
            }
        });
    }

    public void showDeleteDialog(final SortUser user) {
        new AlertDialog.Builder(ctx).setMessage(R.string.contact_deleteContact)
                .setPositiveButton(R.string.common_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog dialog1 = showSpinnerDialog();
                        AVUser.getCurrentUser(LeanchatUser.class).removeFriend(user.getInnerUser().getObjectId(), new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                dialog1.dismiss();
                                if (filterException(e)) {
                                    forceRefresh();
                                }
                            }
                        });
                    }
                }).setNegativeButton(R.string.chat_common_cancel, null).show();
    }

    class ListHeaderViewHolder {
        @InjectView(R.id.iv_msg_tips)
        ImageView msgTipsView;

        @OnClick(R.id.layout_new)
        void goNewFriend() {
            Intent intent = new Intent(ctx, ContactNewFriendActivity.class);
            ctx.startActivity(intent);
        }

        @OnClick(R.id.layout_group)
        void goGroupConvList() {
            //TODO 群组
//            Intent intent = new Intent(ctx, ConversationGroupListActivity.class);
//            ctx.startActivity(intent);
        }

        public ImageView getMsgTipsView() {
            return msgTipsView;
        }
    }

    private class LetterListViewListener implements
            EnLetterView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(String s) {
            int position = userAdapter.getPositionForSection(s.charAt(0));
            if (position != -1) {
                friendsList.setSelection(position);
            }
        }
    }

    public static class PinyinComparator implements Comparator<SortUser> {
        public int compare(SortUser o1, SortUser o2) {
            if (o1.getSortLetters().equals("@")
                    || o2.getSortLetters().equals("#")) {
                return -1;
            } else if (o1.getSortLetters().equals("#")
                    || o2.getSortLetters().equals("@")) {
                return 1;
            } else {
                return o1.getSortLetters().compareTo(o2.getSortLetters());
            }
        }
    }

    public void forceRefresh() {
        AVUser curUser = AVUser.getCurrentUser();
        AVQuery<LeanchatUser> q = null;
        try {
            q = curUser.followeeQuery(LeanchatUser.class);
        } catch (Exception e) {
            //在 currentUser.objectId 为 null 的时候抛出的，不做处理
            e.printStackTrace();
        }

        q.clearCachedResult();
        friendsList.onRefresh();
    }

    public static List<AVUser> findFriends() throws Exception {
        final List<AVUser> friends = new ArrayList<AVUser>();
        final AVException[] es = new AVException[1];
        final CountDownLatch latch = new CountDownLatch(1);
        LeanchatUser.getCurrentUser(LeanchatUser.class).findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<LeanchatUser>() {
            @Override
            public void done(List<LeanchatUser> avUsers, AVException e) {
                if (e != null) {
                    es[0] = e;
                } else {
                    friends.addAll(avUsers);
                }
                latch.countDown();
            }
        });
        latch.await();
        if (es[0] != null) {
            throw es[0];
        } else {
            List<String> userIds = new ArrayList<String>();
            for (AVUser user : friends) {
                userIds.add(user.getObjectId());
            }
            CacheService.setFriendIds(userIds);
            CacheService.cacheUsers(userIds);
            List<AVUser> newFriends = new ArrayList<>();
            for (AVUser user : friends) {
                newFriends.add(CacheService.lookupUser(user.getObjectId()));
            }
            return newFriends;
        }
    }

    public void onEvent(ContactRefreshEvent event) {
        refresh();
    }

    public void onEvent(InvitationEvent event) {
        //TODO 会不会有问题
        AddRequestManager.getInstance().unreadRequestsIncrement();
        updateNewRequestBadge();
    }
}

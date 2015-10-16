package com.lxy.test.whv.ui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avoscloud.leanchatlib.controller.ChatManager;
import com.avoscloud.leanchatlib.controller.ConversationHelper;
import com.avoscloud.leanchatlib.model.ConversationType;
import com.avoscloud.leanchatlib.model.Room;
import com.avoscloud.leanchatlib.utils.AVUserCacheUtils;
import com.lxy.test.whv.App;
import com.lxy.test.whv.R;
import com.lxy.test.whv.service.AddRequestManager;
import com.lxy.test.whv.service.ConversationManager;
import com.lxy.test.whv.service.event.ContactRefreshEvent;
import com.lxy.test.whv.service.event.InvitationEvent;
import com.lxy.test.whv.ui.base_activity.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by LXY on 2015/10/16.
 * 联系人 & 朋友请求 & chat  不再保留单独的联系人选项卡，建议使用搜索功能来查找自己的朋友
 */
public class ContactFragment extends BaseFragment implements ChatManager.ConnectionListener {

    @InjectView(R.id.im_client_state_view)
    View imClientStateView;

    @InjectView(R.id.fragment_conversation_srl_pullrefresh)
    protected SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.fragment_conversation_srl_view)
    protected RecyclerView recyclerView;

    protected ConversationListAdapter<Room> itemAdapter;
    protected LinearLayoutManager layoutManager;

    // 包括新的朋友 群组
    @InjectView(R.id.head_layout)
    View listHeaderView;
    private ListHeaderViewHolder listHeaderViewHolder = new ListHeaderViewHolder();

    private ConversationManager conversationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        ButterKnife.inject(this, view);
        ButterKnife.inject(listHeaderViewHolder, listHeaderView);

        conversationManager = ConversationManager.getInstance();
        refreshLayout.setEnabled(false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        itemAdapter = new ConversationListAdapter<Room>();
        recyclerView.setAdapter(itemAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeader();
        refresh();
        updateConversationList();
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

    private void initHeader() {
        headerLayout.showTitle(App.ctx.getString(R.string.contact));
        headerLayout.showRightImageButton(R.drawable.base_action_bar_add_bg_selector, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 查找&新朋友功能 修改 隐藏?
                Intent intent = new Intent(ctx, ContactAddFriendActivity.class);
                ctx.startActivity(intent);
            }
        });
    }

    //TODO 自动刷新
    private void updateConversationList() {
        conversationManager.findAndCacheRooms(new Room.MultiRoomsCallback() {
            @Override
            public void done(List<Room> roomList, AVException exception) {
                if (filterException(exception)) {

                    updateLastMessage(roomList);
                    cacheRelatedUsers(roomList);

                    List<Room> sortedRooms = sortRooms(roomList);
                    itemAdapter.setDataList(sortedRooms);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private List<Room> sortRooms(final List<Room> roomList) {
        List<Room> sortedList = new ArrayList<Room>();
        if (null != roomList) {
            sortedList.addAll(roomList);
            Collections.sort(sortedList, new Comparator<Room>() {
                @Override
                public int compare(Room lhs, Room rhs) {
                    long value = lhs.getLastModifyTime() - rhs.getLastModifyTime();
                    if (value > 0) {
                        return -1;
                    } else if (value < 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
        return sortedList;
    }

    private void updateLastMessage(final List<Room> roomList) {
        for (final Room room : roomList) {
            AVIMConversation conversation = room.getConversation();
            if (null != conversation) {
                conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
                    @Override
                    public void done(AVIMMessage avimMessage, AVIMException e) {
                        if (filterException(e) && null != avimMessage) {
                            room.setLastMessage(avimMessage);
                            int index = roomList.indexOf(room);
                            itemAdapter.notifyItemChanged(index);
                        }
                    }
                });
            }
        }
    }

    private void cacheRelatedUsers(List<Room> rooms) {
        List<String> needCacheUsers = new ArrayList<String>();
        for (Room room : rooms) {
            AVIMConversation conversation = room.getConversation();
            if (ConversationHelper.typeOfConversation(conversation) == ConversationType.Single) {
                needCacheUsers.add(ConversationHelper.otherIdOfConversation(conversation));
            }
        }
        AVUserCacheUtils.cacheUsers(needCacheUsers, new AVUserCacheUtils.CacheUserCallback() {
            @Override
            public void done(Exception e) {
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateNewRequestBadge() {
        listHeaderViewHolder.getMsgTipsView().setVisibility(
                AddRequestManager.getInstance().hasUnreadRequests() ? View.VISIBLE : View.GONE);
    }

    public void forceRefresh() {
        //TODO 用户切换使用?
    }

    private void refresh() {
        AddRequestManager.getInstance().countUnreadRequests(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                updateNewRequestBadge();
            }
        });
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
            //TODO
//            Intent intent = new Intent(ctx, ConversationGroupListActivity.class);
//            ctx.startActivity(intent);
        }

        public ImageView getMsgTipsView() {
            return msgTipsView;
        }
    }

    public void onEvent(ContactRefreshEvent event) {
        forceRefresh();
    }

    public void onEvent(InvitationEvent event) {
        AddRequestManager.getInstance().unreadRequestsIncrement();
        updateNewRequestBadge();
    }

    @Override
    public void onConnectionChanged(boolean connect) {
        imClientStateView.setVisibility(connect ? View.GONE : View.VISIBLE);
    }
}

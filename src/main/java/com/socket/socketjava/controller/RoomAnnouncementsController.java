package com.socket.socketjava.controller;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.socket.socketjava.domain.pojo.RoomAnnouncements;
import com.socket.socketjava.result.Result;
import com.socket.socketjava.service.IRoomAnnouncementsService;
import com.socket.socketjava.utils.holder.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 群公告表 前端控制器
 * </p>
 *
 * @author 哞哞
 * @since 2025-03-29
 */
@RestController("/room")
@RequestMapping("/room-announcements")
@Tag(name = "群公告管理")
public class RoomAnnouncementsController {

    @Autowired
    private IRoomAnnouncementsService roomAnnouncementsService;

    @PostMapping("/announcement")
    @Operation(summary = "发布群公告")
    public Result pushAnnouncement(Integer roomId, String content, String attachmentUrls, Integer isPinned) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        roomAnnouncementsService.publishAnnouncement(roomId, content, attachmentUrls, isPinned, userId);
        return Result.ok("公告已发布");
    }

    @PutMapping("/announcement")
    @Operation(summary = "修改群公告")
    public Result putAnnouncement(Integer announcementId, String content, String attachmentUrls, Integer isPinned) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        roomAnnouncementsService.updateAnnouncement(announcementId, content, attachmentUrls, isPinned, userId);
        return Result.ok("公告修改成功");
    }

    @DeleteMapping("/announcement")
    @Operation(summary = "删除群公告")
    public Result deleteAnnouncement(Integer announcementId, Integer status) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        roomAnnouncementsService.deleteAnnouncement(announcementId, status, userId);
        return Result.ok("公告已删除");
    }

    @PostMapping("/pin")
    @Operation(summary = "置顶群公告")
    public Result pinAnnouncement(Integer announcementId, Integer isPinned) {
        Integer userId = UserHolder.getLoginHolder().getUserId();
        roomAnnouncementsService.pinAnnouncement(announcementId, isPinned, userId);
        return Result.ok(isPinned == 1 ? "公告已置顶" : "公告已取消置顶");
    }

    @Operation(summary = "分页查询群公告")
    @GetMapping("/announcement")
    public Result getAnnouncement(Integer roomId,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<RoomAnnouncements> resultPage = roomAnnouncementsService.getAnnouncementPage(roomId, pageNum, pageSize);
        return Result.ok(resultPage);
    }

}

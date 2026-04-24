package com.openmanagement.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.openmanagement.workflow.domain.entity.WfTask;
import com.openmanagement.workflow.vo.WfTaskVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<WfTask> {

    @Select("SELECT t.*, COALESCE(u.real_name, u.username) AS assignee_name " +
            "FROM wf_task t LEFT JOIN sys_user u ON t.assignee_id = u.id AND u.deleted_flag = 0 " +
            "WHERE t.assignee_id = #{assigneeId} AND t.status = 'PENDING' AND t.deleted_flag = 0 " +
            "ORDER BY t.created_at DESC, t.id DESC")
    List<WfTaskVO> selectPendingTasksWithAssigneeName(Long assigneeId);
}

package dao;

import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Author:BY
 * Date:2020/3/24
 * Description:
 */
@Repository("insertRequestLog")
public interface InsertRequestLogDao {
    void insertLog(Map map);
}

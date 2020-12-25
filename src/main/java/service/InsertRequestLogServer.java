package service;

import dao.InsertRequestLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Author:BY
 * Date:2020/3/25
 * Description:
 */
@Service
public class InsertRequestLogServer {
    private final InsertRequestLogDao insertRequestLogDao;

    @Autowired
    public InsertRequestLogServer(InsertRequestLogDao insertRequestLogDao) {
        this.insertRequestLogDao = insertRequestLogDao;
    }

    public void intoLog(Map map) {
        insertRequestLogDao.insertLog(map);
    }
}

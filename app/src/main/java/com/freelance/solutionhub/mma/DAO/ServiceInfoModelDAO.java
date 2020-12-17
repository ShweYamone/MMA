package com.freelance.solutionhub.mma.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.freelance.solutionhub.mma.model.ServiceInfoModel;

import java.util.List;

@Dao
public interface ServiceInfoModelDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ServiceInfoModel> serviceInfoModels);

    @Query("SELECT * FROM ServiceInfoModel WHERE id LIKE 'pm%'")
    List<ServiceInfoModel> getPMServices();

    @Query("SELECT * FROM ServiceInfoModel WHERE id LIKE 'cm%'")
    List<ServiceInfoModel> getCMServices();

    @Query("DELETE from ServiceInfoModel WHERE id LIKE 'pm%'")
    void deletePMServices();

    @Query("DELETE from ServiceInfoModel WHERE id LIKE 'cm%'")
    void deleteCMServices();

    @Query("Select count(*) from ServiceInfoModel")
    int getNumberOfServices();
}

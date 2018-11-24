package dk.loej.hc.loot.repository;

import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import dk.loej.hc.loot.entity.LootItem;

public interface LootItemRepository extends CrudRepository<LootItem, Integer> {
	public List<LootItem> findByLootDateOrderByPrioritySequenceAsc(Date lootDate);	
	@Query("select l from LootItem l where l.lootDate = :lootDate order by prioritySequence asc")
    Stream<LootItem> findByLootDateOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate);
		
	
	public List<LootItem> findByLootDateAndOriginalOrderByPrioritySequenceAsc(Date lootDate, boolean original);
	@Query("select l from LootItem l where l.lootDate = :lootDate and l.original = :original order by prioritySequence asc")
    Stream<LootItem> findByLootDateAndOriginalOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate, @Param("original") boolean original);
	
	public List<LootItem> findByLootDateAndPlayerIdAndOriginalOrderByPrioritySequenceAsc(Date lootDate, Integer playerId, boolean common);
	@Query("select l from LootItem l where l.lootDate = :lootDate and l.playerId = :playerId and l.original = :original order by prioritySequence asc")
    Stream<LootItem> findByLootDateAndPlayerIdAndOriginalAndDisabledOrderByPrioritySequenceAscReturnStream(@Param("lootDate") Date lootDate, @Param("playerId") Integer playerId, @Param("original") boolean original);
	
	
	

}

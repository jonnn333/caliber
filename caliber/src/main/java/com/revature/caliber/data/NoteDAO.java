package com.revature.caliber.data;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.revature.caliber.beans.Note;
import com.revature.caliber.beans.NoteType;
import com.revature.caliber.beans.Trainee;
import com.revature.caliber.beans.TrainingStatus;

/**
 * 
 * @author Patrick Walsh
 * @author Ateeb Khawaja
 *
 */
@Repository
public class NoteDAO {

	private static final Logger log = Logger.getLogger(NoteDAO.class);
	private SessionFactory sessionFactory;
	private static final String BATCH = "batch";
	private static final String B_TRAINEES = "b.trainees";
	private static final String T_TRAINING_STATUS = "t.trainingStatus";
	private static final String B_BATCH_ID = "b.batchId";
	private static final String TRAINEE = "trainee";
	private static final String T_TRAINEE_ID = "t.traineeId";
	private static final String QC_FEEDBACK = "qcFeedback";
	private static final int MONTHS_BACK = -1;
	private static final String BATCH_BATCH_ID = "batch.batchId";


	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Save a new note
	 * 
	 * @param note
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int save(Note note) {
		log.info("Saving Note " + note);
		return (int) sessionFactory.getCurrentSession().save(note);
	}

	/**
	 * Update note contents
	 * 
	 * @param note
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void update(Note note) {
		log.info("Updating Note " + note);
		sessionFactory.getCurrentSession().saveOrUpdate(note);
	}

	/**
	 * Returns all batch-level notes for a given week. Only notes written by
	 * trainers are returned.
	 * 
	 * @param batchId
	 * @param week
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findBatchNotes(Integer batchId, Integer week) {
		log.info("Finding batch notes for week " + week + BATCH + batchId);
		return sessionFactory.getCurrentSession().createCriteria(Note.class).createAlias(BATCH, "b")
				.createAlias(B_TRAINEES, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq(B_BATCH_ID, batchId))
				.add(Restrictions.eq("week", week.shortValue()))
				.add(Restrictions.eq(QC_FEEDBACK, false))
				.add(Restrictions.eq("type", NoteType.BATCH))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	}

	/**
	 * Returns all individual notes for a given week. Only notes written by
	 * trainers are returned.
	 * 
	 * @param traineeId
	 * @param week
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findIndividualNotes(Integer batchId, Integer week) {
		log.info("Finding individual notes for week " + week + " " + BATCH + batchId);
		return sessionFactory.getCurrentSession().createCriteria(Note.class)
				.createAlias(TRAINEE, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped)).createAlias("t.batch", "b")
				.add(Restrictions.eq(B_BATCH_ID, batchId))
				.add(Restrictions.eq("week", week.shortValue()))
				.add(Restrictions.eq("type", NoteType.TRAINEE))
				.add(Restrictions.eq(QC_FEEDBACK, false))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Returns Trainee note for the week
	 * 
	 * @param traineeId
	 * @param week
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Note findTraineeNote(Integer traineeId, Integer week) {
		return (Note) sessionFactory.getCurrentSession().createCriteria(Note.class).setFetchMode(BATCH, FetchMode.JOIN)
				.createAlias(TRAINEE, "t").add(Restrictions.eq(T_TRAINEE_ID, traineeId))
				.add(Restrictions.eq("week", week.shortValue())).add(Restrictions.eq("type", NoteType.TRAINEE))
				.add(Restrictions.eq(QC_FEEDBACK, false))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).uniqueResult();
	}

	/**
	 * Returns QCTrainee note for the week(Michael)
	 * 
	 * @param traineeId
	 * @param week
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Note findQCTraineeNote(Integer traineeId, Integer week) {
		return (Note) sessionFactory.getCurrentSession().createCriteria(Note.class).setFetchMode(BATCH, FetchMode.JOIN)
				.createAlias(TRAINEE, "t").add(Restrictions.eq(T_TRAINEE_ID, traineeId))
				.add(Restrictions.eq("week", week.shortValue())).add(Restrictions.eq("type", NoteType.QC_TRAINEE))
				.add(Restrictions.eq(QC_FEEDBACK, true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
	}

	/**
	 * Returns batch note for the batch for the week
	 * 
	 * @param batchId
	 * @param week
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Note findQCBatchNotes(Integer batchId, Integer week) {
		log.info("Finding QC batch notes for week " + week + " " + BATCH + batchId);
		return (Note) sessionFactory.getCurrentSession().createCriteria(Note.class).createAlias(BATCH, "b")
				.createAlias(B_TRAINEES, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq("type", NoteType.QC_BATCH))
				.add(Restrictions.eq(BATCH_BATCH_ID, batchId)).add(Restrictions.eq("week", week.shortValue()))
				.add(Restrictions.eq(QC_FEEDBACK, true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
	}
	
	/**
	 * Returns all batch-level notes for a particular batch.
	 * 
	 * @param batchId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllBatchQcNotes(Integer batchId) {
		log.info("Finding All batch notes for batch " + BATCH + batchId);
		Calendar endDateLimit = Calendar.getInstance();	
		endDateLimit.add(Calendar.MONTH, MONTHS_BACK);
		return sessionFactory.getCurrentSession().createCriteria(Note.class).createAlias(BATCH, "b")
				.createAlias(B_TRAINEES, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq("type", NoteType.QC_BATCH))
				.add(Restrictions.eq(B_BATCH_ID, batchId))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	/**
	 * Returns all individual notes written by QC for a given week.
	 * 
	 * @param traineeId
	 * @param week
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findQCIndividualNotes(Integer traineeId, Integer week) {
		log.info("Finding QC individual notes for week " + week + " for trainee: " + traineeId);
		return sessionFactory.getCurrentSession().createCriteria(Note.class)
				.createAlias(TRAINEE, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq(T_TRAINEE_ID, traineeId))
				.add(Restrictions.eq("week", week.shortValue()))
				.add(Restrictions.eq("type", NoteType.QC_TRAINEE))
				.add(Restrictions.eq(QC_FEEDBACK, true))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Returns all batch-level notes for a given week.
	 * 
	 * @param batchId
	 * @param week
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllBatchNotes(Integer batchId, Integer week) {
		log.info("Finding All batch notes for week " + week + " " + BATCH + batchId);
		return sessionFactory.getCurrentSession().createCriteria(Note.class).createAlias(BATCH, "b")
				.createAlias(B_TRAINEES, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq("type", NoteType.BATCH))
				.add(Restrictions.eq(BATCH_BATCH_ID, batchId)).add(Restrictions.eq("week", week.shortValue()))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Returns all individual trainee notes for a particular week.
	 * 
	 * @param traineeId
	 * @param week
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllIndividualNotes(Integer traineeId, Integer week) {
		log.info("Finding All individual notes for week " + week + " for trainee: " + traineeId);
		return sessionFactory.getCurrentSession().createCriteria(Note.class)
				.createAlias(TRAINEE, "t", JoinType.LEFT_OUTER_JOIN).createAlias(BATCH, "b", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq("type", NoteType.TRAINEE))
				.add(Restrictions.eq(T_TRAINEE_ID, traineeId))
				.add(Restrictions.eq("week", week.shortValue()))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Returns all notes for a particular trainee. Used for cumulative reporting
	 * on a single trainee. Only trainer notes are displayed.
	 * 
	 * @param traineeId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllPublicIndividualNotes(Integer traineeId) {
		log.info("Finding All individual notes for trainee: " + traineeId);
		return sessionFactory.getCurrentSession().createCriteria(Note.class)
				.createAlias(TRAINEE, "t", JoinType.LEFT_OUTER_JOIN).createAlias(BATCH, "b", JoinType.LEFT_OUTER_JOIN)
				.createAlias(B_TRAINEES, "batchmates", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq("type", NoteType.TRAINEE))
				.add(Restrictions.eq(T_TRAINEE_ID, traineeId)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.addOrder(Order.asc("week")).list();
	}

	/**
	 * Returns all qc notes for a batch
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllQCBatchNotes(Integer batchId) {
		log.info("Find All QC Batch notes");
		return sessionFactory.getCurrentSession().createCriteria(Note.class).createAlias(BATCH, "b")
				.createAlias(B_TRAINEES, "t", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq(B_BATCH_ID, batchId)).add(Restrictions.eq(QC_FEEDBACK, true))
				.add(Restrictions.eq("type", NoteType.QC_BATCH))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("week")).list();
	}

	/**
	 * Returns all QC notes for trainee in the batch for the week
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllQCTraineeNotes(Integer batchId, Integer week) {
		log.info("Find All QC Trainee notes");
		List<Note> noteList = sessionFactory.getCurrentSession().createCriteria(Note.class)
				.createAlias(TRAINEE, "t", JoinType.INNER_JOIN)
				.createAlias(BATCH, "bt", JoinType.INNER_JOIN)
				.createAlias("bt.trainees", "btT")
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped)).createAlias("t.batch", "b")
				.add(Restrictions.eq(B_BATCH_ID, batchId)).add(Restrictions.eq("week", week.shortValue()))
				.add(Restrictions.eq(QC_FEEDBACK, true)).add(Restrictions.eq("type", NoteType.QC_TRAINEE))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("week")).list();
//		List<Note> noteList = sessionFactory.getCurrentSession().createCriteria(Note.class)
//				.createAlias(TRAINEE, "t", JoinType.INNER_JOIN)
//				.createAlias(BATCH, "bt", JoinType.INNER_JOIN)
//				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped)).createAlias("t.batch", "b")
//				.add(Restrictions.eq(B_BATCH_ID, batchId)).add(Restrictions.eq("week", week.shortValue()))
//				.add(Restrictions.eq("type", NoteType.TRAINEE))
//				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("week")).list();
		log.info("Retrive the noteList");
		for(Note note: noteList){
			if(note.getBatch() != null) {
				for(Trainee trainee: note.getBatch().getTrainees()){
					//log.info(trainee);
					Hibernate.initialize(trainee);
				}
			}
		}
		return noteList;
	}

	/**
	 * Returns all QC notes for trainee in the batch
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Note> findAllQCTraineeOverallNotes(Integer traineeId) {
		log.info("Find All QC Trainee notes for that trainee");
		return sessionFactory.getCurrentSession().createCriteria(Note.class)
				.createAlias(TRAINEE, "t", JoinType.LEFT_OUTER_JOIN).createAlias(BATCH, "b", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.ne(T_TRAINING_STATUS, TrainingStatus.Dropped))
				.add(Restrictions.eq(T_TRAINEE_ID, traineeId)).add(Restrictions.eq(QC_FEEDBACK, true))
				.add(Restrictions.eq("type", NoteType.QC_TRAINEE))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(Order.asc("week")).list();
	}
}
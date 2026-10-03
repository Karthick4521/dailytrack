package com.dailytrack.service;
import com.dailytrack.dto.*; import com.dailytrack.entity.*; import com.dailytrack.repository.*;
import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException; import java.security.Principal; import java.time.*; import java.util.*; import java.util.stream.*;
@Service @Transactional
public class TrackService {
 private final UserRepo users; private final TrackerRepo tr; private final RecordRepo rr; private final GoalRepo gr;
 public TrackService(UserRepo u,TrackerRepo t,RecordRepo r,GoalRepo g){ users=u;tr=t;rr=r;gr=g; }
 static ResponseStatusException nf(){ return new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found"); }
 static Map<String,Object> m(Object... kv){ Map<String,Object> r=new LinkedHashMap<>(); for(int i=0;i<kv.length;i+=2) r.put((String)kv[i],kv[i+1]); return r; }
 public Long uid(Principal p){ return users.findByEmail(p.getName()).orElseThrow().id; }
 public Map<String,Object> me(Long u){ AppUser a=users.findById(u).orElseThrow(); return m("id",a.id,"fullName",a.fullName,"email",a.email,"createdAt",a.createdAt); }
 public List<Tracker> trackers(Long u,boolean all){ return all?tr.findByUserId(u):tr.findByUserIdAndArchivedFalse(u); }
 public Tracker tracker(Long u,Long id){ return tr.findByIdAndUserId(id,u).orElseThrow(TrackService::nf); }
 public Tracker saveTracker(Long u,Long id,TrackerReq q){ Tracker t=id==null?new Tracker():tracker(u,id);
  if(id==null){ t.userId=u; t.createdAt=LocalDate.now(); } t.name=q.name().trim(); t.category=q.category(); t.type=q.type(); t.unit=q.unit(); if(q.archived()!=null) t.archived=q.archived(); return tr.save(t); }
 public void deleteTracker(Long u,Long id){ Tracker t=tracker(u,id); rr.deleteByTrackerId(id); gr.deleteByTrackerId(id); tr.delete(t); }
 public List<DailyRecord> records(Long u,LocalDate f,LocalDate t){ return rr.findByUserIdAndRecordDateBetweenOrderByRecordDateDesc(u,f,t); }
 public DailyRecord saveRecord(Long u,Long id,RecordReq q){ tracker(u,q.trackerId());
  LocalDate d=q.date()==null?LocalDate.now():q.date(); if(d.isAfter(LocalDate.now().plusDays(1))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date can't be in the future");
  DailyRecord r=id==null?new DailyRecord():rr.findByIdAndUserId(id,u).orElseThrow(TrackService::nf);
  r.userId=u; r.trackerId=q.trackerId(); r.recordDate=d; r.value=q.value(); r.notes=q.notes(); return rr.save(r); }
 public void deleteRecord(Long u,Long id){ rr.delete(rr.findByIdAndUserId(id,u).orElseThrow(TrackService::nf)); }
 // each calendar date counts once toward a streak
 static int[] streak(Set<LocalDate> d){ LocalDate t=LocalDate.now(),c=d.contains(t)?t:t.minusDays(1); int cur=0; while(d.contains(c)){cur++;c=c.minusDays(1);}
  int best=0,run=0; LocalDate prev=null; for(LocalDate x:new TreeSet<>(d)){ run=prev!=null&&prev.plusDays(1).equals(x)?run+1:1; best=Math.max(best,run); prev=x; } return new int[]{cur,best}; }
 public Map<String,Object> stats(Long u){
  LocalDate today=LocalDate.now(); List<Tracker> act=tr.findByUserIdAndArchivedFalse(u); Set<Long> ids=act.stream().map(t->t.id).collect(Collectors.toSet());
  List<DailyRecord> all=rr.findByUserId(u); Map<LocalDate,Set<Long>> byDay=new HashMap<>();
  for(DailyRecord d:all) byDay.computeIfAbsent(d.recordDate,k->new HashSet<>()).add(d.trackerId);
  int total=ids.size(); java.util.function.Function<LocalDate,Integer> pct=d->{ if(total==0) return 0; long n=byDay.getOrDefault(d,Set.of()).stream().filter(ids::contains).count(); return (int)Math.round(100.0*n/total); };
  Set<LocalDate> actDays=byDay.entrySet().stream().filter(e->e.getValue().stream().anyMatch(ids::contains)).map(Map.Entry::getKey).collect(Collectors.toSet());
  int[] st=streak(actDays); List<Object> week=new ArrayList<>(); int sum30=0,active30=0;
  for(int i=6;i>=0;i--){ LocalDate d=today.minusDays(i); week.add(m("date",d.toString(),"pct",pct.apply(d))); }
  for(int i=0;i<30;i++){ LocalDate d=today.minusDays(i); sum30+=pct.apply(d); if(actDays.contains(d)) active30++; }
  List<Object> per=new ArrayList<>();
  for(Tracker t:act){ List<DailyRecord> rs=all.stream().filter(r->r.trackerId.equals(t.id)).toList();
   int[] s=streak(rs.stream().map(r->r.recordDate).collect(Collectors.toSet()));
   per.add(m("id",t.id,"name",t.name,"category",t.category,"type",t.type,"unit",t.unit,"today",rs.stream().filter(r->r.recordDate.equals(today)).mapToDouble(r->r.value).sum(),"current",s[0],"longest",s[1],"total",rs.stream().mapToDouble(r->r.value).sum())); }
  return m("todayDone",byDay.getOrDefault(today,Set.of()).stream().filter(ids::contains).count(),"todayTotal",total,"current",st[0],"longest",st[1],"active30",active30,"completion30",sum30/30,"week",week,"trackers",per); }
 public List<Object> calendar(Long u,YearMonth ym){ int total=tr.findByUserIdAndArchivedFalse(u).size(); Map<LocalDate,Set<Long>> by=new TreeMap<>();
  for(DailyRecord d:records(u,ym.atDay(1),ym.atEndOfMonth())) by.computeIfAbsent(d.recordDate,k->new HashSet<>()).add(d.trackerId);
  return by.entrySet().stream().map(e->(Object)m("date",e.getKey().toString(),"done",e.getValue().size(),"total",Math.max(total,e.getValue().size()))).toList(); }
 public List<Object> goals(Long u){ LocalDate t=LocalDate.now(); return gr.findByUserId(u).stream().map(g->{
  LocalDate f=switch(g.period){case "daily"->t; case "weekly"->t.with(DayOfWeek.MONDAY); default->t.withDayOfMonth(1);};
  double p=g.trackerId==null?0:rr.findByUserIdAndTrackerIdAndRecordDateBetween(u,g.trackerId,f,t).stream().mapToDouble(d->d.value).sum();
  return (Object)m("id",g.id,"title",g.title,"trackerId",g.trackerId,"target",g.target,"period",g.period,"progress",p); }).toList(); }
 public Goal saveGoal(Long u,Long id,GoalReq q){ if(q.trackerId()!=null) tracker(u,q.trackerId()); Goal g=id==null?new Goal():gr.findByIdAndUserId(id,u).orElseThrow(TrackService::nf);
  g.userId=u; g.title=q.title().trim(); g.trackerId=q.trackerId(); g.target=q.target(); g.period=q.period()==null?"daily":q.period(); return gr.save(g); }
 public void deleteGoal(Long u,Long id){ gr.delete(gr.findByIdAndUserId(id,u).orElseThrow(TrackService::nf)); } }

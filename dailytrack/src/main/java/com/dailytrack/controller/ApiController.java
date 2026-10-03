package com.dailytrack.controller;
import com.dailytrack.dto.*; import com.dailytrack.entity.*; import com.dailytrack.service.TrackService;
import jakarta.validation.Valid; import java.security.Principal; import java.time.*; import java.util.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api")
public class ApiController {
 private final TrackService s; public ApiController(TrackService s){ this.s=s; }
 @GetMapping("/me") Map<String,Object> me(Principal p){ return s.me(s.uid(p)); }
 @GetMapping("/trackers") List<Tracker> trackers(Principal p,@RequestParam(defaultValue="false") boolean all){ return s.trackers(s.uid(p),all); }
 @PostMapping("/trackers") Tracker addT(Principal p,@Valid @RequestBody TrackerReq r){ return s.saveTracker(s.uid(p),null,r); }
 @GetMapping("/trackers/{id}") Tracker getT(Principal p,@PathVariable Long id){ return s.tracker(s.uid(p),id); }
 @PutMapping("/trackers/{id}") Tracker putT(Principal p,@PathVariable Long id,@Valid @RequestBody TrackerReq r){ return s.saveTracker(s.uid(p),id,r); }
 @DeleteMapping("/trackers/{id}") void delT(Principal p,@PathVariable Long id){ s.deleteTracker(s.uid(p),id); }
 @GetMapping("/records") List<DailyRecord> recs(Principal p,@RequestParam LocalDate from,@RequestParam LocalDate to){ return s.records(s.uid(p),from,to); }
 @PostMapping("/records") DailyRecord addR(Principal p,@Valid @RequestBody RecordReq r){ return s.saveRecord(s.uid(p),null,r); }
 @PutMapping("/records/{id}") DailyRecord putR(Principal p,@PathVariable Long id,@Valid @RequestBody RecordReq r){ return s.saveRecord(s.uid(p),id,r); }
 @DeleteMapping("/records/{id}") void delR(Principal p,@PathVariable Long id){ s.deleteRecord(s.uid(p),id); }
 @GetMapping("/calendar") List<Object> cal(Principal p,@RequestParam String month){ return s.calendar(s.uid(p),YearMonth.parse(month)); }
 @GetMapping({"/stats","/streaks"}) Map<String,Object> stats(Principal p){ return s.stats(s.uid(p)); }
 @GetMapping("/goals") List<Object> goals(Principal p){ return s.goals(s.uid(p)); }
 @PostMapping("/goals") Goal addG(Principal p,@Valid @RequestBody GoalReq r){ return s.saveGoal(s.uid(p),null,r); }
 @PutMapping("/goals/{id}") Goal putG(Principal p,@PathVariable Long id,@Valid @RequestBody GoalReq r){ return s.saveGoal(s.uid(p),id,r); }
 @DeleteMapping("/goals/{id}") void delG(Principal p,@PathVariable Long id){ s.deleteGoal(s.uid(p),id); } }

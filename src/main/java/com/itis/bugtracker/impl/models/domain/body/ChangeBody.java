package com.itis.bugtracker.impl.models.domain.body;

import com.itis.bugtracker.impl.models.data.Change;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class ChangeBody {

    private TaskBody taskBody;

    private String target;

    private Date date;

    public static ChangeBody from(Change change) {
        return new ChangeBody(TaskBody.from(change.getTask()), change.getTarget().toString(), change.getDate());
    }

    public static List<ChangeBody> from(List<Change> changeList) {
        return changeList.stream().map(ChangeBody::from).collect(Collectors.toList());
    }
}

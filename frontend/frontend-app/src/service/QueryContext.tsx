import * as React from "react";
import {ChosenKnowledgeFile, DEFAULT} from "../model/KnowledgeFile";

export interface Query {
    queryString: string,
    knowledgeFileTarget: ChosenKnowledgeFile
}

export const QueryContext = React.createContext({queryString: "", knowledgeFileTarget: DEFAULT})

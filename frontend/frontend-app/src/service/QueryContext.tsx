import * as React from "react";
import {DEFAULT, KnowledgeFile} from "../model/KnowledgeFile";

export interface Query {
    queryString: string,
    knowledgeFileTarget: KnowledgeFile
}

export const QueryContext = React.createContext({queryString: "", knowledgeFileTarget: DEFAULT})

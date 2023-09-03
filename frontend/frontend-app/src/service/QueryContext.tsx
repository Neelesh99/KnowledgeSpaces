import * as React from "react";
import {DEFAULT, KnowledgeFile} from "../model/KnowledgeFile";
import {SPACESDEFAULT} from "../model/KnowledgeSpace"

export interface Query {
    queryString: string,
    knowledgeFileTarget: KnowledgeFile
}

export const QueryContext = React.createContext({queryString: "", knowledgeFileTarget: DEFAULT})
export const SpaceQueryContext = React.createContext({queryString: "", knowledgeSpaceTarget: SPACESDEFAULT})
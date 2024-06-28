
export interface MediaPath {
  id?: string;
  mediaFiles?: MediaFileShort[] | MediaFile[];
  name?: string;
  category?: string;
}

export interface MediaFileShort {
  id: string;
  name: string;
  fullPath: string;
  extension: string;
  provider: string;
  size: string;
  error: boolean;
  selectedForTranscoding: boolean;
}


export interface MediaFile {
  id?: string;
  name?: string;
  fullPath?: string;
  extension?: string;
  provider?: string;
  streams?: Stream[];
  format?: {
    filename?: string;
    nbStreams?: number;
    duration?: string;
  };
  size?: string;
  error?: boolean;
  selectedForTranscoding?: boolean;
}

export interface StreamTags {
  language: string | null;
  bps: string | null;
  bpsEng: string | null;
  duration: string | null;
  durationEng: string | null;
  numberOfFrames: string | null;
  numberOfFramesEng: string | null;
  numberOfBytes: string | null;
  numberOfBytesEng: string | null;
}

export interface Stream {
  index: number;
  codecName: string;
  codecType: string;
  width: number;
  height: number;
  channels: number;
  bitRate: string | null;
  channelLayout: string | null;
  tags: StreamTags;
}

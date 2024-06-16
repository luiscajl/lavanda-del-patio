export interface Bt4g {
  id: string
  name: string;
  url: string;
  magnet: string;
  magnetHash: string;
  createTime: number,
  fileSize: string,
  seeders: number,
  leechers: number,
  downloaded?: boolean,
  updated?: string,
  files?: []
}
